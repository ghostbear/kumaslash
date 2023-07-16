package me.ghostbear.kumaslash.data.tachiyomi;

import me.ghostbear.kumaslash.data.tachiyomi.client.ExtensionDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ExtensionMapper {

	public List<Extension> map(List<ExtensionDTO> remote) {
		return remote.stream().map(this::map).toList();
	}

	public Extension map(ExtensionDTO remote) {
		return new Extension(
				remote.pkg(),
				remote.name(),
				remote.apk(),
				remote.lang(),
				remote.code(),
				remote.version(),
				remote.nsfw() == 1,
				remote.hasReadme() == 1,
				remote.hasChangelog() == 1,
				remote.sources()
						.stream()
						.map(new SourceMapper()::map)
						.collect(Collectors.toSet())
		);
	}

}
