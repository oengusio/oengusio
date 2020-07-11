export class Question {

  id: number;
  label: string;
  fieldType: string;
  required: boolean;
  options: string[];
  questionType: string;
  description: string;
  position: number;

  constructor(questionType: string) {
    this.options = [];
    this.fieldType = 'TEXT';
    this.required = false;
    this.questionType = questionType;
  }
}
