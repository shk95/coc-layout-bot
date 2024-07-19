package io.github.shk95.coclayoutbot.repository.jpa.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.Assert;


@Converter
public class ProcessedConverter implements AttributeConverter<YoutubeVideoEntity.Processed, Integer> {

	@Override
	public Integer convertToDatabaseColumn(YoutubeVideoEntity.Processed attribute) {
		if (attribute == null) {
			return YoutubeVideoEntity.Processed.NOT_PROCESSED.getValue();
		}
		return attribute.getValue();
	}

	@Override
	public YoutubeVideoEntity.Processed convertToEntityAttribute(Integer dbData) {
		assert dbData != null;
		Assert.notNull(dbData, "The database value must not be null.");
		for (YoutubeVideoEntity.Processed status : YoutubeVideoEntity.Processed.values()) {
			if (status.getValue() == dbData) {
				return status;
			}
		}
		throw new IllegalArgumentException("An error occurred while converting the database value to the entity attribute. YoutubeVideoEntity.Processed: " + dbData);
	}

}
