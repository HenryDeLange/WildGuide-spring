package mywild.wildguide.framework.logic;

public interface BaseMapper {

    default String trim(String value) {
        return value != null ? value.trim() : null;
    }

}
