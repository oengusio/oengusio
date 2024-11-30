package app.oengus.factory;

import net.datafaker.Faker;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.NonNull;

public abstract class AbstractFactory<T> implements FactoryBean<T> {
    protected Faker faker = new Faker();

    @NonNull
    @Override
    abstract public T getObject();

    @Override
    public boolean isSingleton() {
        return false;
    }

//    @Override abstract public Class<T> getObjectType();
}
