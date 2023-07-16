package me.ghostbear.kumaslash.data.tachiyomi;

import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("extension")
public record Extension(
		@Id
		@Column("package_name")
		String packageName,
		@Column("name")
		String name,
		@Column("file_name")
		String fileName,
		@Column("language")
		String language,
		@Column("code")
		long code,
		@Column("version")
		String version,
		@Column("is_nsfw")
		boolean isNsfw,
		@Column("has_readme")
		boolean hasReadme,
		@Column("has_changelog")
		boolean hasChangelog,
		@Transient
		Set<Source> sources,
		@Transient
		boolean isNew
) implements Persistable<String> {

	@PersistenceCreator
	public Extension(String packageName, String name, String fileName, String language, long code, String version, boolean isNsfw, boolean hasReadme, boolean hasChangelog) {
		this(packageName, name, fileName, language, code, version, isNsfw, hasReadme, hasChangelog, null, false);
	}

	public Extension(String packageName, String name, String fileName, String language, long code, String version, boolean isNsfw, boolean hasReadme, boolean hasChangelog, Set<Source> sources) {
		this(packageName, name, fileName, language, code, version, isNsfw, hasReadme, hasChangelog, sources, false);
	}

	@Override
	public String getId() {
		return packageName;
	}

	public Extension withIsNew(boolean isNew) {
		return new Extension(packageName, name, fileName, language, code, version, isNsfw, hasReadme, hasChangelog, sources, isNew);
	}
}
