package app.oengus.entity.comparator;

import app.oengus.entity.model.Answer;

import java.util.Comparator;

public class AnswerComparator implements Comparator<Answer> {

	@Override
	public int compare(final Answer o1, final Answer o2) {
//        return Integer.compare(o1.getId(), o2.getId());
        return Integer.compare(o1.getQuestion().getPosition(), o2.getQuestion().getPosition());
	}
}
