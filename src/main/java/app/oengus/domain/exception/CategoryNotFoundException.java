package app.oengus.domain.exception;

import app.oengus.domain.exception.base.GenericNotFoundException;

public class CategoryNotFoundException extends GenericNotFoundException {
    public CategoryNotFoundException() {
        super("Category not found");
    }
}
