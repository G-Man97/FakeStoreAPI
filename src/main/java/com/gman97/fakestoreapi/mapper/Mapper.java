package com.gman97.fakestoreapi.mapper;

public interface Mapper <F, T> {

    T map(F obj);

    default T map(F fromObject, T toObject) {
        return toObject;
    }
}
