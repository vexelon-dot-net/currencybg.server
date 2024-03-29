package net.vexelon.currencybg.srv.db;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.FieldPath;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.vexelon.currencybg.srv.Defs;
import net.vexelon.currencybg.srv.GlobalConfig;
import net.vexelon.currencybg.srv.db.adapters.*;
import net.vexelon.currencybg.srv.db.models.CurrencyData;
import net.vexelon.currencybg.srv.db.models.CurrencySource;
import net.vexelon.currencybg.srv.db.models.ReportData;
import net.vexelon.currencybg.srv.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public final class FirestoreDataSource implements DataSource {

	private static final Logger log = LoggerFactory.getLogger(FirestoreDataSource.class);

	private static GoogleCredentials credentials = null;
	private        Firestore         db          = null;
	private        Gson              gson        = null;

	FirestoreDataSource() {
	}

	public static void setCredentials(@Nonnull GoogleCredentials credentials) {
		FirestoreDataSource.credentials = credentials;
	}

	private String toJson(Collection<CurrencyData> currencies) {
		if (gson == null) {
			gson = new GsonBuilder().setDateFormat(Defs.DATEFORMAT_ISO_8601).create();
		}
		return gson.toJson(currencies, new TypeToken<Collection<CurrencyData>>() {}.getType());
	}

	@Override
	public void connect() throws DataSourceException {
		try {
			this.db = FirestoreOptions.getDefaultInstance().toBuilder()
					.setProjectId(GlobalConfig.INSTANCE.getGcpProjectId()).setCredentials(credentials).build()
					.getService();
		} catch (Exception e) {
			throw new DataSourceException("Could not open Firestore client channels!", e);
		}
	}

	@Override
	public String getAllCurrentRatesAfter(Date timeFrom) throws DataSourceException {
		try {
			var currencies = db.collection("currencies");
			var snapshot = currencies.whereGreaterThan("date", timeFrom)
					.whereLessThan("date", DateTimeUtils.addDays(timeFrom, 1)).get();
			var adapter = new FirestoreToCurrencyDataAdapter();
			var result = snapshot.get().getDocuments().stream().map(adapter::fromEntity).toList();

			return toJson(result);
		} catch (Exception e) {
			throw new DataSourceException("Error fetching currencies from Firestore!", e);
		}
	}

	@Override
	public String getAllCurrentRatesAfter(int sourceId, Date timeFrom) throws DataSourceException {
		try {
			var currencies = db.collection("currencies");
			var snapshot = currencies.whereEqualTo("source_id", sourceId).whereGreaterThan("date", timeFrom)
					.whereLessThan("date", DateTimeUtils.addDays(timeFrom, 1)).get();
			var adapter = new FirestoreToCurrencyDataAdapter();

			return toJson(snapshot.get().getDocuments().stream().map(adapter::fromEntity).toList());
		} catch (Exception e) {
			throw new DataSourceException("Error fetching currencies from Firestore! (sourceId=%d)".formatted(sourceId),
					e);
		}
	}

	@Override
	public String getAllRates(Date dateFrom) throws DataSourceException {
		try {
			var currencies = db.collection("currencies");
			var snapshot = currencies.whereGreaterThanOrEqualTo("date", DateTimeUtils.getStartOfDay(dateFrom))
					.whereLessThanOrEqualTo("date", DateTimeUtils.getEndOfDay(dateFrom)).get();
			var adapter = new FirestoreToCurrencyDataAdapter();
			var result = snapshot.get().getDocuments().stream().map(adapter::fromEntity).toList();

			return toJson(result);
		} catch (Exception e) {
			throw new DataSourceException("Error fetching currencies from Firestore!", e);
		}
	}

	@Override
	public String getAllRates(int sourceId, Date dateFrom) throws DataSourceException {
		try {
			var currencies = db.collection("currencies");
			var snapshot = currencies.whereEqualTo("source_id", sourceId)
					.whereGreaterThanOrEqualTo("date", DateTimeUtils.getStartOfDay(dateFrom))
					.whereLessThanOrEqualTo("date", DateTimeUtils.getEndOfDay(dateFrom)).get();
			var adapter = new FirestoreToCurrencyDataAdapter();

			return toJson(snapshot.get().getDocuments().stream().map(adapter::fromEntity).toList());
		} catch (Exception e) {
			throw new DataSourceException("Error fetching currencies from Firestore! (sourceId=%d)".formatted(sourceId),
					e);
		}
	}

	@Override
	public void cleanupRates(int ageInDays) throws DataSourceException {
		try {
			var notAfter = DateTimeUtils.addDays(new Date(), -ageInDays);
			if (log.isDebugEnabled()) {
				log.debug("Delete all currency rates older than: {}", notAfter);
			}

			var currencies = db.collection("currencies");
			var snapshot = currencies.whereLessThan("date", notAfter).get();

			var documents = snapshot.get().getDocuments();
			log.info("Found {} currency rates to delete", documents.size());

			if (!documents.isEmpty()) {
				var partitions = Lists.partition(documents, 500);
				var batch = db.batch();

				for (var partition : partitions) {
					for (var doc : partition) {
						batch.delete(doc.getReference());
					}
				}

				var results = batch.commit().get();
				log.info("Deleted {} currency rates.", results.size());
			}
		} catch (Exception e) {
			throw new DataSourceException("Error deleting currency rates from Firestore!", e);
		}
	}

	@Nonnull
	@Override
	public Collection<CurrencySource> getAllSources(boolean isActiveOnly) throws DataSourceException {
		try {
			var sources = db.collection("sources");
			var snapshot = isActiveOnly ?
					sources.whereEqualTo("status", CurrencySource.STATUS_ENABLED).get() :
					sources.get();
			var adapter = new FirestoreToCurrencySourceAdapter();

			return snapshot.get().getDocuments().stream().map(adapter::fromEntity).toList();
		} catch (Exception e) {
			throw new DataSourceException(
					"Error fetching sources from Firestore! activeOnly = %b".formatted(isActiveOnly), e);
		}
	}

	@Override
	public boolean isCheckAuthentication(String authenticationKey) throws DataSourceException {
		try {
			var apiKeys = db.collection("api_keys");
			var snapshot = apiKeys.whereEqualTo("value", authenticationKey).get();
			return !snapshot.get().isEmpty();
		} catch (Exception e) {
			throw new DataSourceException("Error querying auth key in Firestore!", e);
		}
	}

	@Override
	public void addRates(Map<Integer, List<CurrencyData>> rates) throws DataSourceException {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void addRates(Collection<CurrencyData> rates) throws DataSourceException {
		try {
			var currencies = db.collection("currencies");
			var adapter = new CurrencyDataToFirestoreAdapter();
			for (var currencyData : rates) {
				currencies.add(adapter.fromEntity(currencyData));
			}
		} catch (Exception e) {
			throw new DataSourceException("Error adding currency data to Firestore!", e);
		}
	}

	@Override
	public void updateSource(int sourceId, CurrencySource source) throws DataSourceException {
		try {
			var sources = db.collection("sources");
			var querySnapshot = sources.whereEqualTo("source_id", sourceId).get().get();
			var queryDoc = querySnapshot.getDocuments().iterator().next();

			sources.document(queryDoc.getId()).update(new CurrencySourceToFirestoreAdapter().fromEntity(source)).get();
		} catch (Exception e) {
			throw new DataSourceException("Error updating source (%d) in Firestore!".formatted(source.getSourceId()),
					e);
		}
	}

	@Override
	public void addReportMessage(String message) throws DataSourceException {
		try {
			var reports = db.collection("reports");
			reports.add(Map.of("message", message, "created_on", FieldValue.serverTimestamp()));
		} catch (Exception e) {
			throw new DataSourceException("Error adding report to Firestore!", e);
		}
	}

	@Nonnull
	@Override
	public Collection<ReportData> getReports() throws DataSourceException {
		try {
			var reports = db.collection("reports").get();
			var adapter = new FirestoreToReportDataAdapter();
			return reports.get().getDocuments().stream().map(adapter::fromEntity).toList();
		} catch (Exception e) {
			throw new DataSourceException("Error fetching reports from Firestore!", e);
		}
	}

	@Override
	public void deleteReports(@Nonnull Collection<ReportData> reports) throws DataSourceException {
		try {
			var future = db.collection("reports");
			var snapshot = future.whereIn(FieldPath.documentId(),
					reports.stream().map(ReportData::getDocumentId).toList()).get();
			for (var doc : snapshot.get().getDocuments()) {
				doc.getReference().delete();
			}
		} catch (Exception e) {
			throw new DataSourceException("Error deleting %d reports from Firestore!".formatted(reports.size()), e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			db.close();
		} catch (Exception e) {
			throw new IOException("Error closing Firestore client channels!", e);
		}
	}
}
