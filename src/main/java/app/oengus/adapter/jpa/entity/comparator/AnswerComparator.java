package app.oengus.adapter.jpa.entity.comparator;

import app.oengus.adapter.jpa.entity.AnswerEntity;

import java.util.Comparator;

public class AnswerComparator implements Comparator<AnswerEntity> {

    @Override
    public int compare(final AnswerEntity o1, final AnswerEntity o2) {
        return Integer.compare(o1.getQuestion().getPosition(), o2.getQuestion().getPosition());
    }
}
