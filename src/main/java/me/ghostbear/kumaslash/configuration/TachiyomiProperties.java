package me.ghostbear.kumaslash.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "tachiyomi")
public class TachiyomiProperties {

	private Extensions extensions = new Extensions();
	private List<Flavour> flavours = Collections.emptyList();

	public Extensions getExtensions() {
		return extensions;
	}

	public void setExtensions(Extensions extensions) {
		this.extensions = extensions;
	}

	public List<Flavour> getFlavours() {
		return flavours;
	}

	public void setFlavours(List<Flavour> flavours) {
		this.flavours = flavours;
	}

	@Override
	public String toString() {
		return "TachiyomiProperties{" +
				"extensions=" + extensions +
				", flavours=" + flavours +
				'}';
	}

	public static class Extensions {
		private String organization = "tachiyomiorg";
		private String repository = "tachiyomi-extensions";
		private String branch = "repo";
		private String location = "index.min.json";

		public String getOrganization() {
			return organization;
		}

		public void setOrganization(String organization) {
			this.organization = organization;
		}

		public String getRepository() {
			return repository;
		}

		public void setRepository(String repository) {
			this.repository = repository;
		}

		public String getBranch() {
			return branch;
		}

		public void setBranch(String branch) {
			this.branch = branch;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		@Override
		public String toString() {
			return "Extensions{" +
					"organization='" + organization + '\'' +
					", repository='" + repository + '\'' +
					", branch='" + branch + '\'' +
					", location='" + location + '\'' +
					'}';
		}
	}

	public static class Flavour {
		private String owner;
		private String repository;
		private String name;
		private String pattern;

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getRepository() {
			return repository;
		}

		public void setRepository(String repository) {
			this.repository = repository;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPattern() {
			return pattern;
		}

		public void setPattern(String pattern) {
			this.pattern = pattern;
		}

		@Override
		public String toString() {
			return "Flavour{" +
					"owner='" + owner + '\'' +
					", repository='" + repository + '\'' +
					", name='" + name + '\'' +
					", pattern='" + pattern + '\'' +
					'}';
		}
	}

}
