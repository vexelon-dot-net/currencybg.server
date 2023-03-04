package net.vexelon.currencybg.srv.db.adapters;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import net.vexelon.currencybg.srv.db.models.ReportData;

import javax.annotation.Nonnull;

public class FirestoreReportDataAdapter implements DataSourceAdapter<QueryDocumentSnapshot, ReportData> {

	private final Gson gson = new Gson();

	@Nonnull
	@Override
	public ReportData fromSourceEntity(QueryDocumentSnapshot entity) {
		var report = new ReportData();
		report.setDocumentId(entity.getId());
		//		report.setId(entity.getLong("id").intValue());
		report.setCreatedOn(entity.getTimestamp("created_on").toDate());
		//		report.setSource(entity.getLong("source_id").intValue());
		report.setMessage(entity.getString("message"));
		return report;
	}
}
