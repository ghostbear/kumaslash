package me.ghostbear.kumaslash.tachiyomi.local.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("source")
public record Source(
		@Id
		@Column("id")
		String id,
		@Column("name")
		String name,
		@Column("language")
		String language,
		@Column("base_url")
		String baseUrl,
		@Column("version_id")
		long versionId,
		@Column("has_cloudflare")
		boolean hasCloudflare,
		@Column("fk_package_name")
		String packageName,
		@Transient
		Extension extension,
		@Transient
		boolean isNew
) implements Persistable<String> {

	@Override
	public String getId() {
		return id;
	}

	public Source(String id, String name, String language, String baseUrl, long versionId, boolean hasCloudflare) {
		this(id, name, language, baseUrl, versionId, hasCloudflare, null, null, false);
	}

	@PersistenceCreator
	public Source(String id, String name, String language, String baseUrl, long versionId, boolean hasCloudflare, String packageName) {
		this(id, name, language, baseUrl, versionId, hasCloudflare, packageName, null, false);
	}

	public Source withIsNew(boolean isNew) {
		return new Source(id, name, language, baseUrl, versionId, hasCloudflare, packageName, extension, isNew);
	}

	public Source withExtension(String packageName) {
		return new Source(id, name, language, baseUrl, versionId, hasCloudflare, packageName, extension, isNew);
	}

	public Source withExtension(Extension extension) {
		return new Source(id, name, language, baseUrl, versionId, hasCloudflare, extension.packageName(), extension, isNew);
	}
}
