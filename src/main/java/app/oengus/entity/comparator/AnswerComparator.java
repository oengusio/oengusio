package app.oengus.entity.comparator;

import app.oengus.entity.model.AnswerEntity;

import java.util.Comparator;

public class AnswerComparator implements Comparator<AnswerEntity> {

	@Override
	public int compare(final AnswerEntity o1, final AnswerEntity o2) {
        return Integer.compare(o1.getQuestion().getPosition(), o2.getQuestion().getPosition());
	}
}
