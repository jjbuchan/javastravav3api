package javastrava.api.v3.model.reference;

import javastrava.config.Messages;

/**
 * <p>
 * Type of photo - currently Instagram only
 * </p>
 * 
 * @author Dan Shannon
 *
 */
public enum StravaPhotoType {
	INSTAGRAM(Messages.getString("StravaPhotoType.instagram"), Messages.getString("StravaPhotoType.instagram.description")),  //$NON-NLS-1$ //$NON-NLS-2$
	UNKNOWN(Messages.getString("Common.unknown"), Messages.getString("Common.unknown.description")); //$NON-NLS-1$ //$NON-NLS-2$

	private String	id;
	private String	description;

	private StravaPhotoType(final String id, final String description) {
		this.id = id;
		this.description = description;
	}

	// @JsonValue
	public String getValue() {
		return this.id;
	}

	// @JsonCreator
	public static StravaPhotoType create(final String id) {
		StravaPhotoType[] types = StravaPhotoType.values();
		for (StravaPhotoType type : types) {
			if (type.getId().equals(id)) {
				return type;
			}
		}
		return StravaPhotoType.UNKNOWN;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return this.id;
	}
}
