package app.oengus.entity.comparator;

import app.oengus.entity.model.Answer;

import java.util.Comparator;

public class AnswerComparator implements Comparator<Answer> {

	@Override
	public int compare(final Answer o1, final Answer o2) {
		return o1.getQuestion().getPosition().compareTo(o2.getQuestion().getPosition());
	}
}
