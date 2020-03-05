package com.github.beloshabskiy.telegram.domain.dialogue


import spock.lang.Specification

class TicketSearchDialogueTest extends Specification {

    def 'should process round-trip scenario'() {
        setup:
            def underTest = new TicketSearchDialogue()
        when: 'initial message'
            def message = underTest.initiate()
        then:
            message == 'IATA-код аэропорта вылета?'
        when: 'incorrect answer'
            def answer = underTest.answer('Москва')
        then:
            answer.message == 'Некорректный ответ, попробуйте ещё раз'
            answer.options.isEmpty()
        when: 'one more incorrect answer'
            answer = underTest.answer('КОД')
        then:
            answer.message == 'Некорректный ответ, попробуйте ещё раз'
            answer.options.isEmpty()
        when: 'correct "from"'
            answer = underTest.answer('LED')
        then:
            answer.message == 'Куда?'
            answer.options.size() == 2
            answer.options[0] == 'Код аэропорта'
            answer.options[1] == 'Куда угодно'
        when: 'chosen "Код аэропорта"'
            answer = underTest.answer('Код аэропорта')
        then:
            answer.message == 'IATA-код аэропорта назначения?'
            answer.options.isEmpty()
        when: 'incorrect answer'
            answer = underTest.answer('КОД')
        then:
            answer.message == 'Некорректный ответ, попробуйте ещё раз'
            answer.options.isEmpty()
        when: 'correct "to"'
            answer = underTest.answer('JFK')
        then:
            answer.message == 'Когда?'
            answer.options.size() == 2
            answer.options[0] == 'Точная дата'
            answer.options[1] == 'Диапазон дат'
        when: 'exact date'
            answer = underTest.answer('Точная дата')
        then:
            answer.message == 'Дата в формате dd/MM/yyyy?'
            answer.options.isEmpty()
        when: 'incorrect date'
            answer = underTest.answer('09.10.1993')
        then:
            answer.message == 'Некорректный ответ, попробуйте ещё раз'
            answer.options.isEmpty()
        when: 'correct date'
            answer = underTest.answer('09/10/2020')
        then:
            answer.message == 'Обратный билет?'
            answer.options.size() == 2
            answer.options[0] == 'Да'
            answer.options[1] == 'Нет'
        when: 'yes, with return ticket'
            answer = underTest.answer('Да')
        then:
            answer.message == 'Когда обратно?'
            answer.options.size() == 2
            answer.options[0] == 'Точная дата'
            answer.options[1] == 'Диапазон дат'
        when: 'exact return date'
            answer = underTest.answer('Точная дата')
        then:
            answer.message == 'Дата в формате dd/MM/yyyy?'
            answer.options.isEmpty()
        when:
            answer = underTest.answer('09/11/2020')
        then:
            answer.message == 'Ищу билеты'
            answer.options.isEmpty()
        when: 'Dialogue is finished'
            def request = underTest.buildRequest()
        then:
            request.from == 'LED'
            request.to == 'JFK'
            request.dateFrom == '09/10/2020'
            request.dateTo == '09/10/2020'
            request.returnDateFrom == '09/11/2020'
            request.returnDateTo == '09/11/2020'
    }

    def 'should process one-way scenario'() {
        setup:
            def underTest = new TicketSearchDialogue()
        when: 'from LED to anywhere'
            underTest.initiate()
            underTest.answer('LED')
            def answer = underTest.answer('Куда угодно')
        then:
            answer.message == 'Когда?'
            answer.options.size() == 2
            answer.options[0] == 'Точная дата'
            answer.options[1] == 'Диапазон дат'
        when: 'chosen date range'
            answer = underTest.answer('Диапазон дат')
        then:
            answer.message == 'Начало диапазона в формате dd/MM/yyyy?'
            answer.options.isEmpty()
        when: 'incorrect date'
            answer = underTest.answer('09.10.1993')
        then:
            answer.message == 'Некорректный ответ, попробуйте ещё раз'
            answer.options.isEmpty()
        when: 'correct date'
            answer = underTest.answer('09/10/2020')
        then:
            answer.message == 'Конец диапазона в формате dd/MM/yyyy?'
            answer.options.isEmpty()
        when: 'correct date'
            answer = underTest.answer('09/11/2020')
        then:
            answer.message == 'Обратный билет?'
            answer.options.size() == 2
            answer.options[0] == 'Да'
            answer.options[1] == 'Нет'
        when: 'no, return ticket is not needed'
            answer = underTest.answer('Нет')
        then:
            answer.message == 'Ищу билеты'
            answer.options.isEmpty()
        when: 'Dialogue is finished'
            def request = underTest.buildRequest()
        then:
            request.from == 'LED'
            request.to == null
            request.dateFrom == '09/10/2020'
            request.dateTo == '09/11/2020'
            request.returnDateFrom == null
            request.returnDateTo == null
    }

    def 'should process round-trip with return date range'() {
        setup:
            def underTest = new TicketSearchDialogue()
        when: 'initial part'
            underTest.initiate()
            underTest.answer('LED')
            underTest.answer('Код аэропорта')
            underTest.answer('JFK')
            underTest.answer('Точная дата')
            underTest.answer('09/10/2020')
            underTest.answer('Да')
            def answer = underTest.answer('Диапазон дат')
        then:
            answer.message == 'Начало диапазона в формате dd/MM/yyyy?'
            answer.options.isEmpty()
        when: 'correct date'
            answer = underTest.answer('09/12/2020')
        then:
            answer.message == 'Конец диапазона в формате dd/MM/yyyy?'
            answer.options.isEmpty()
        when: 'correct date'
            answer = underTest.answer('19/12/2020')
        then:
            answer.message == 'Ищу билеты'
            answer.options.isEmpty()
        when: 'Dialogue is finished'
            def request = underTest.buildRequest()
        then:
            request.from == 'LED'
            request.to == 'JFK'
            request.dateFrom == '09/10/2020'
            request.dateTo == '09/10/2020'
            request.returnDateFrom == '09/12/2020'
            request.returnDateTo == '19/12/2020'
    }
}
