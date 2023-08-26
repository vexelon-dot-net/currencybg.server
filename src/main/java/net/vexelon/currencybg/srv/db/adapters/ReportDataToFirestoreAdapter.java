package net.vexelon.currencybg.srv.db.adapters;

import com.google.cloud.firestore.FieldValue;
import net.vexelon.currencybg.srv.db.models.ReportData;

import javax.annotation.Nonnull;
import java.util.Map;

public class ReportDataToFirestoreAdapter implements DataSourceAdapter<ReportData, Map<String, Object>> {

	@Nonnull
	@Override
	public Map<String, Object> fromEntity(ReportData entity) {
		return Map.of("message", entity.getMessage(), "created_on", FieldValue.serverTimestamp());
	}
}
