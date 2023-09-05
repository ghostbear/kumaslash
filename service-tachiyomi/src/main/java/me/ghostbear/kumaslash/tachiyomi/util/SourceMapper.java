package me.ghostbear.kumaslash.tachiyomi.util;

import me.ghostbear.kumaslash.tachiyomi.local.model.Source;
import me.ghostbear.kumaslash.tachiyomi.remote.model.SourceDTO;

public class SourceMapper {

	public Source map(SourceDTO remote) {
		return new Source(
				remote.id(),
				remote.name(),
				remote.lang(),
				remote.baseUrl(),
				remote.versionId(),
				remote.hasCloudflare() == 1
		);
	}

}
