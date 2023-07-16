package me.ghostbear.kumaslash.data.tachiyomi;

import me.ghostbear.kumaslash.data.tachiyomi.client.SourceDTO;

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
