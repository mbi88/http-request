package com.mbi;

class Director {

    private final Builder builder;

    Director(Builder builder) {
        this.builder = builder;
    }

    Request construct() {
        builder.buildParameters();

        return builder.build();
    }
}
