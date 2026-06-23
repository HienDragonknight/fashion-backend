package com.fashion.util;

/**
 * Utility for resolving bilingual (Vietnamese / English) content fields.
 *
 * <p>Design contract:
 * <ul>
 *   <li>Vietnamese is always the primary / fallback language.</li>
 *   <li>English value is served only when the caller explicitly requests "en"
 *       AND the English value is non-null and non-blank.</li>
 *   <li>If the English value is missing, Vietnamese is returned silently — no
 *       empty strings are ever surfaced to the frontend.</li>
 * </ul>
 *
 * <p>Usage in a service:
 * <pre>{@code
 *   String lang = LocaleUtils.fromHeader(acceptLanguage);
 *   dto.setName(LocaleUtils.resolve(product.getName(), product.getNameEn(), lang));
 * }</pre>
 */
public final class LocaleUtils {

    private LocaleUtils() {}

    /** Supported locale codes. */
    public static final String VI = "vi";
    public static final String EN = "en";

    /**
     * Resolves the correct locale string from an HTTP Accept-Language header value.
     * Defaults to Vietnamese for any unrecognised or null value.
     *
     * @param acceptLanguage raw header value, e.g. "en", "en-US,en;q=0.9", "vi-VN"
     * @return "en" or "vi"
     */
    public static String fromHeader(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.isBlank()) return VI;
        // Accept-Language can be "en-US,en;q=0.9" — check primary tag only
        String primary = acceptLanguage.split("[,;]")[0].trim().toLowerCase();
        return primary.startsWith("en") ? EN : VI;
    }

    /**
     * Returns the localised value for a bilingual field.
     *
     * @param vi   Vietnamese value (primary, never null in a well-formed record)
     * @param en   English value (nullable — null means "not yet translated")
     * @param lang locale code returned by {@link #fromHeader}
     * @return resolved string, guaranteed non-null (falls back to empty string as last resort)
     */
    public static String resolve(String vi, String en, String lang) {
        if (EN.equalsIgnoreCase(lang) && en != null && !en.isBlank()) {
            return en;
        }
        return vi != null ? vi : "";
    }

    /**
     * Nullable variant — returns null instead of empty string when the primary Vi value is null.
     * Useful for optional fields like descriptions.
     */
    public static String resolveNullable(String vi, String en, String lang) {
        if (EN.equalsIgnoreCase(lang) && en != null && !en.isBlank()) {
            return en;
        }
        return vi; // may be null
    }
}
