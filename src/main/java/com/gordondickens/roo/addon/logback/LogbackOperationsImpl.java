package com.gordondickens.roo.addon.logback;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.classpath.PhysicalTypeDetails;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.PhysicalTypeMetadataProvider;
import org.springframework.roo.classpath.details.MemberFindingUtils;
import org.springframework.roo.classpath.details.MutableClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectMetadata;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.project.Property;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.TemplateUtils;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * Provides Logback configuration services
 * 
 * @author Gordon Dickens
 * @since 1.1
 */
@Component
@Service
public class LogbackOperationsImpl implements LogbackOperations {
	private static Logger logger = Logger.getLogger(LogbackOperations.class
			.getName());
	@Reference
	private FileManager fileManager;
	@Reference
	private MetadataService metadataService;
	@Reference
	private PhysicalTypeMetadataProvider physicalTypeMetadataProvider;
	@Reference
	private ProjectOperations projectOperations;
	@Reference
	private PathResolver pathResolver;

	public boolean isCommandAvailable() {
		return getPathResolver() != null;
	}

	public boolean isInstallLogbackAvailable() {
		ProjectMetadata project = (ProjectMetadata) metadataService.get(ProjectMetadata.getProjectIdentifier());
		if (project == null) {
			return false;
		}

		// Only permit installation if they don't already have some version of Spring Security installed
		// TODO Configure to recognize any version of Logback
		return project.getDependenciesExcludingVersion(new Dependency("ch.qos.logback", "logback-classic", "${logback.version}")).size() == 0;
	}
	
	
	public void annotateType(JavaType javaType) {
		Assert.notNull(javaType, "Java type required");

		String id = physicalTypeMetadataProvider.findIdentifier(javaType);
		if (id == null) {
			throw new IllegalArgumentException("Cannot locate source for '"
					+ javaType.getFullyQualifiedTypeName() + "'");
		}

		// Obtain the physical type and itd mutable details
		PhysicalTypeMetadata ptm = (PhysicalTypeMetadata) metadataService
				.get(id);
		Assert.notNull(ptm, "Java source code unavailable for type "
				+ PhysicalTypeIdentifier.getFriendlyName(id));
		PhysicalTypeDetails ptd = ptm.getPhysicalTypeDetails();
		Assert.notNull(ptd, "Java source code details unavailable for type "
				+ PhysicalTypeIdentifier.getFriendlyName(id));
		Assert.isInstanceOf(MutableClassOrInterfaceTypeDetails.class, ptd,
				"Java source code is immutable for type "
						+ PhysicalTypeIdentifier.getFriendlyName(id));
		MutableClassOrInterfaceTypeDetails mutableTypeDetails = (MutableClassOrInterfaceTypeDetails) ptd;

		if (null == MemberFindingUtils.getAnnotationOfType(mutableTypeDetails
				.getAnnotations(), new JavaType(RooLogback.class.getName()))) {
			JavaType rooRooLogback = new JavaType(RooLogback.class.getName());
			if (!mutableTypeDetails.getAnnotations().contains(rooRooLogback)) {
				AnnotationMetadataBuilder annotationBuilder = new AnnotationMetadataBuilder(
						rooRooLogback);
				mutableTypeDetails.addTypeAnnotation(annotationBuilder.build());
			}
		}
	}

	public void setup() {
		// Parse the configuration.xml file
		Element configuration = XmlUtils.getConfiguration(getClass());

		// Add POM properties
		updatePomProperties(configuration);

		// Add dependencies to POM
		updateDependencies(configuration);

		// TODO Add File creation of logback.xml into src/main/resources
		// Copy the template across
		String destination = pathResolver.getIdentifier(
				Path.SRC_MAIN_RESOURCES, "logback.xml");
		if (!fileManager.exists(destination)) {
			try {
				FileCopyUtils.copy(TemplateUtils.getTemplate(getClass(),
						"logback-template.xml"),
						fileManager.createFile(destination).getOutputStream());
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			}
		}

	}

	private void updatePomProperties(Element configuration) {
		List<Element> databaseProperties = XmlUtils.findElements(
				"/configuration/logback/properties/*", configuration);
		for (Element property : databaseProperties) {
			projectOperations.addProperty(new Property(property));
		}
	}

	private void updateDependencies(Element configuration) {
		List<Element> dependencies = XmlUtils
				.findElements("/configuration/logback/dependencies/dependency",
						configuration);
		for (Element dependency : dependencies) {
			projectOperations.dependencyUpdate(new Dependency(dependency));
		}
		dependencies = XmlUtils.findElements(
				"/configuration/logback/remove/dependencies/dependency",
				configuration);
		for (Element dependency : dependencies) {
			projectOperations.removeDependency(new Dependency(dependency));
		}
	}

	/**
	 * @return the path resolver or null if there is no user project
	 */
	private PathResolver getPathResolver() {
		ProjectMetadata projectMetadata = (ProjectMetadata) metadataService
				.get(ProjectMetadata.getProjectIdentifier());
		if (projectMetadata == null) {
			return null;
		}
		return projectMetadata.getPathResolver();
	}
}