/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.ArtifactProvider;
import opennlp.tools.util.model.ArtifactSerializer;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.util.ConfigPropertiesUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

/**
 * Custom implementation of {@link BaseToolFactory} for PoS-tagger.
 * <p>
 * Description of the {@link MorphDictionary} injection:
 * <ul>
 * <li>training: passed as a part of contextGenerator, during the serialization
 * of contextGenerator store only a version of the dictionary;
 * <li>tagging: passed to a model (with InputStream of serialized artifacts),
 * that is {@link ArtifactProvider}, it will be injected back to a
 * contextGenerator.
 * </ul>
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class POSTaggerFactory extends BaseToolFactory {

	private static final String GRAM_CATEGORIES_MANIFEST_ENTRY_NAME = "gramCategories";
	private static final String FEATURE_EXTRACTORS_ENTRY_NAME = "feature.extractors";

	private FeatureExtractorsBasedContextGenerator contextGenerator;
	private ImmutableSet<String> gramCategories;

	/**
	 * This constructor is required for OpenNLP model deserialization
	 */
	public POSTaggerFactory() {
	}

	public POSTaggerFactory(FeatureExtractorsBasedContextGenerator contextGenerator,
			Collection<String> gramCategories) {
		this.contextGenerator = contextGenerator;
		this.gramCategories = ImmutableSet.copyOf(gramCategories);
	}

	@Override
	protected void init(ArtifactProvider artifactProvider) {
		super.init(artifactProvider);
		String gramCategoriesStr = artifactProvider
				.getManifestProperty(GRAM_CATEGORIES_MANIFEST_ENTRY_NAME);
		if (gramCategoriesStr != null) {
			gramCategories = ImmutableSet.copyOf(gramCatSplitter.split(gramCategoriesStr));
		}
	}

	public BeamSearchContextGenerator<Token> getContextGenerator() {
		if (contextGenerator == null && artifactProvider != null) {
			contextGenerator = artifactProvider.getArtifact(FEATURE_EXTRACTORS_ENTRY_NAME);
		}
		return contextGenerator;
	}

	public Set<String> getGramCategories() {
		return gramCategories;
	}

	@Override
	public Map<String, Object> createArtifactMap() {
		Map<String, Object> artMap = super.createArtifactMap();
		artMap.put(FEATURE_EXTRACTORS_ENTRY_NAME, contextGenerator);
		return artMap;
	}

	@Override
	public Map<String, String> createManifestEntries() {
		Map<String, String> manifest = super.createManifestEntries();
		if (gramCategories != null) {
			manifest.put(GRAM_CATEGORIES_MANIFEST_ENTRY_NAME, gramCatJoiner.join(gramCategories));
		}
		return manifest;
	}

	private static final Splitter gramCatSplitter = Splitter.on(',');
	private static final Joiner gramCatJoiner = Joiner.on(',');

	@Override
	public void validateArtifactMap() throws InvalidFormatException {
		Object featExtractorsEntry = artifactProvider.getArtifact(FEATURE_EXTRACTORS_ENTRY_NAME);
		if (featExtractorsEntry == null) {
			throw new InvalidFormatException("No featureExtractors in artifacts map");
		}
		if (!(featExtractorsEntry instanceof FeatureExtractorsBasedContextGenerator)) {
			throw new InvalidFormatException(String.format(
					"Unknown type of feature extractors aggregate: %s",
					featExtractorsEntry.getClass()));
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map<String, ArtifactSerializer> createArtifactSerializersMap() {
		Map<String, ArtifactSerializer> artSerMap = super.createArtifactSerializersMap();
		artSerMap.put("extractors", new FeatureExtractorsSerializer());
		// artSerMap.put("dict", new MorphologyDictionarySerializer());
		return artSerMap;
	}

	/*
	static class MorphologyDictionarySerializer implements ArtifactSerializer<MorphDictionary> {
		@Override
		public MorphDictionary create(InputStream in) throws IOException, InvalidFormatException {
			// MUST never be called
			// because a dictionary instance is supposed to be managed by UIMA
			throw new UnsupportedOperationException("MorphDictionary should be injected by UIMA!");
			// TOD insert here the code that will check compatibility of the injected dictionary with one that defined in the model
		}

		@Override
		public void serialize(MorphDictionary artifact, OutputStream out) throws IOException {
			// do nothing
		}
	}
	*/

	class FeatureExtractorsSerializer implements
			ArtifactSerializer<FeatureExtractorsBasedContextGenerator> {

		// A serializer instance is hold in a BaseModel instance,
		// so this key will have the same life-time as this BaseModel
		private Object dictCacheKey;

		@Override
		public FeatureExtractorsBasedContextGenerator create(InputStream in) throws IOException,
				InvalidFormatException {
			if (dictCacheKey != null) {
				throw new UnsupportedOperationException();
			}
			Properties props = new Properties();
			props.load(in);
			MorphDictionary dict = null;
			if (ConfigPropertiesUtils.getStringProperty(props,
					DefaultFeatureExtractors.PROP_DICTIONARY_VERSION, false) != null) {
				// load dictionary
				if (artifactProvider == null) {
					throw new IllegalStateException("ArtifactProvider is null");
				}
				dict = artifactProvider.getArtifact(POSModel.MORPH_DICT_ENTRY_NAME);
				if (dict == null) {
					throw new IllegalStateException(
							"ArtifactProvider did not provide expected MorphDictionary");
				}
			}

			return DefaultFeatureExtractors.from(props, dict);
		}

		@Override
		public void serialize(FeatureExtractorsBasedContextGenerator artifact, OutputStream out)
				throws IOException {
			if (!(artifact instanceof DefaultFeatureExtractors)) {
				throw new UnsupportedOperationException();
			}
			Properties props = new Properties();
			DefaultFeatureExtractors.to((DefaultFeatureExtractors) artifact, props);
			props.store(out, "");
		}

	}
}
