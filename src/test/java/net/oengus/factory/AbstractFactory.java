package net.oengus.factory;

import net.datafaker.Faker;
import org.springframework.beans.factory.FactoryBean;

public abstract class AbstractFactory<T> implements FactoryBean<T> {
    protected Faker faker = new Faker();

    @Override
    abstract public T getObject();

    @Override
    public boolean isSingleton() {
        return false;
    }
}
