package mywild.wildguide.domain.entry.data;

public enum ScientificRank {

    /** Ends in "IDAE" or "ACEAE" */
    FAMILY, 

    /** Ends in "INAE" or "OIDEAE" */
    SUBFAMILY,

    /** Ends in "INI" or "EAE" */
    TRIBE,

    /** Ends in "INA" or "INAE" */
    SUBTRIBE,

    GENUS,

    SUBGENUS,

    SPECIES,

    SUBSPECIES,

    /** Variety is for botany, Form/Morph is for Zoology, Aberration is for Lepidopterology */
    VARIETY_FORM_ABERRATION;

}
