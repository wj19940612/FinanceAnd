package com.sbai.httplib;

/**
 * Modified by john on 13/11/2017
 * <p>
 * Description: http error wrap class
 */
public class ApiError<E> {

    private E error;

    public ApiError(E error) {
        this.error = error;
    }

    public E getError() {
        return error;
    }

    @Override
    public String toString() {
        return error.toString();
    }
}
