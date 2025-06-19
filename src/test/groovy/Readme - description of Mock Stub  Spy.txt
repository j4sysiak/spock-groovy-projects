Mock i Stub:
Kiedy tworzysz Mock(MathService) lub Stub(MathService), tworzysz całkowicie nowy, "pusty" obiekt,
który implementuje interfejs MathService.
Jego zachowanie definiujesz od zera.

Stub()
    Bez weryfikacji interakcji
    Skupia się na poprawnym wyniku (result == expected)

Spy:
Spy omija zepsuty mechanizm >>, ponieważ używa prawdziwej implementacji.
Kiedy tworzysz Spy(realImpl), tworzysz obiekt, który opakowuje prawdziwy obiekt (realImpl).
Domyślnie, każde wywołanie metody na Spy jest przekazywane do prawdziwego obiektu (realImpl.multiply(...)).
W Spocku jest to klasyczny przypadek, w którym najlepiej użyć Spy na testowanej klasie,
aby móc "podmienić" zachowanie metody pomocniczej.


------------------------------------------------------------------------------------------------------------

Oto krótkie i konkretne podsumowanie różnic między Mock, Stub, Spy – z myślą o trzech klasach: MockSpec, StubSpec, SpySpec.

✅ 1. Mock – weryfikacja + pełna kontrola interakcji
Użycie: Mock(MathService)

Zachowanie: nic się nie dzieje domyślnie – musisz wszystko >>

Można asercjonować: ile razy metoda została wywołana

Do czego: testowanie interakcji (czy coś zostało wywołane i ile razy)

Przykład:
1 * service.multiply(2, 3) >> 6


✅ 2. Stub – tylko wartości zwrotne, brak weryfikacji
Użycie: Stub(MathService)

Zachowanie: automatycznie odpowiada na metody, ale nie sprawdza interakcji

Nie sprawdzisz: ile razy została wywołana dana metoda

Do czego: testowanie funkcjonalności, nie zachowania

Przykład:
service.multiply(_, _) >> { a, b -> a * b }


✅ 3. Spy – realny obiekt z opcjonalnym nadpisaniem
Użycie: Spy(new MathServiceImpl())

Zachowanie: wykonuje prawdziwy kod klasy

Możesz: nadpisać tylko wybrane wywołania

Do czego: testowanie logiki + warunkowe kontrolowanie zachowania

Przykład:
1 * spy.multiply(2, 3) >> 999 // tylko to nadpisuje, reszta działa realnie