INSERT INTO GatunekFilmu (nazwa) VALUES ('Sci-Fi')/
INSERT INTO GatunekFilmu (nazwa) VALUES ('Documentary')/
INSERT INTO GatunekFilmu (nazwa) VALUES ('Comedy')/
INSERT INTO GatunekFilmu (nazwa) VALUES ('Fantasy')/

INSERT INTO Rezyser (imieLubPseudonim, nazwisko, krajPochodzenia, dataUrodzenia)
VALUES ('James', 'Gunn', 'USA', DATE '1966-08-05')/
INSERT INTO Rezyser (imieLubPseudonim, nazwisko, krajPochodzenia, dataUrodzenia)
VALUES ('Patryk', 'Vega', 'Polska', DATE '1977-01-02')/
INSERT INTO Rezyser (imieLubPseudonim, nazwisko, krajPochodzenia, dataUrodzenia)
VALUES ('Todd', 'Phillips', 'USA', DATE '1970-12-20')/
INSERT INTO Rezyser (imieLubPseudonim, nazwisko, krajPochodzenia, dataUrodzenia)
VALUES ('Chris', 'Columbus', 'USA', DATE '1958-09-10')/

INSERT INTO Aktor (imieLubPseudonim, nazwisko, krajPochodzenia, dataUrodzenia)
VALUES ('Chris', 'Pratt', 'USA', DATE '1979-06-21')/
INSERT INTO Aktor (imieLubPseudonim, nazwisko, krajPochodzenia, dataUrodzenia)
VALUES ('Bradley', 'Cooper', 'USA', DATE '1975-01-05')/
INSERT INTO Aktor (imieLubPseudonim, nazwisko, krajPochodzenia, dataUrodzenia)
VALUES ('Zach', 'Galifianakis', 'USA', DATE '1969-10-01')/
INSERT INTO Aktor (imieLubPseudonim, nazwisko, krajPochodzenia, dataUrodzenia)
VALUES ('Daniel', 'Radcliffe', 'UK', DATE '1989-07-23')/

INSERT INTO Film (tytul, gatunekID, czasTrwania, opis, rezyserID, zalecenieWiekowe, dataPremiery, jezykOryginalny)
VALUES ('Guardians Of The Galaxy 3', 1, 149, 'Test description', 1, '13+', DATE '2023-05-01', 'English')/
INSERT INTO Film (tytul, gatunekID, czasTrwania, opis, rezyserID, zalecenieWiekowe, dataPremiery, jezykOryginalny)
VALUES ('Putin', 2, 130, 'Patryk Vega documenatry', 2, '16+', DATE '2023-09-10', 'Polish')/
INSERT INTO Film (tytul, gatunekID, czasTrwania, opis, rezyserID, zalecenieWiekowe, dataPremiery, jezykOryginalny)
VALUES ('The Hangover', 3, 100, 'Comedy about a group of friends', 3, '18+', DATE '2009-06-02', 'English')/
INSERT INTO Film (tytul, gatunekID, czasTrwania, opis, rezyserID, zalecenieWiekowe, dataPremiery, jezykOryginalny)
VALUES ('Harry Potter and The Chamber Of Secrets', 4, 161, 'Second part of the HP franchise', 4, '13+', DATE '2002-11-15', 'English')/

INSERT INTO FilmAktor (aktorID, filmID) VALUES (1, 1)/
INSERT INTO FilmAktor (aktorID, filmID) VALUES (2, 1)/
INSERT INTO FilmAktor (aktorID, filmID) VALUES (3, 3)/
INSERT INTO FilmAktor (aktorID, filmID) VALUES (4, 4)/

INSERT INTO TypSeansu (nazwa, cenaStandardowa)
VALUES ('2D', 25.00)/
INSERT INTO TypSeansu (nazwa, cenaStandardowa)
VALUES ('3D', 40.00)/
INSERT INTO TypSeansu (nazwa, cenaStandardowa)
VALUES ('IMAX', 55.00)/

INSERT INTO Sala (nazwa, pojemnosc) VALUES ('Hall 1', 150)/
INSERT INTO Sala (nazwa, pojemnosc) VALUES ('Hall 2', 220)/
INSERT INTO Sala (nazwa, pojemnosc) VALUES ('Hall 3', 120)/

INSERT INTO Seans (dataCzas, filmID, salaID, typSeansuID, jezykSeansu, dubbingCzyNapisy, cenaSpecjalna)
VALUES (TIMESTAMP '2030-11-10 18:00:00 +02:00', 1, 1, 1, 'English', 'SUBTITLES', NULL)/
INSERT INTO Seans (dataCzas, filmID, salaID, typSeansuID, jezykSeansu, dubbingCzyNapisy, cenaSpecjalna)
VALUES (TIMESTAMP '2030-11-10 20:00:00 +02:00', 2, 2, 1, 'Polish', NULL, 19.90)/

INSERT INTO RodzajeZnizek (nazwa, procentZnizki) VALUES ('Student', 20)/
INSERT INTO RodzajeZnizek (nazwa, procentZnizki) VALUES ('Senior', 30)/
INSERT INTO RodzajeZnizek (nazwa, procentZnizki) VALUES ('Big Family Discount', 67)/

INSERT INTO Klient (imie, nazwisko, haslo, email, poziomLojalnosciowy)
VALUES ('Jan', 'Kowalski', 'haslo123', 'jan.kowalski@example.com', 1)/
INSERT INTO Klient (imie, nazwisko, haslo, email, poziomLojalnosciowy)
VALUES ('Anna', 'Nowak', 'tajnehaslo', 'anna.nowak@example.com', 2)/

INSERT INTO Pracownik (imie, nazwisko, haslo, email, rolaPracownika, czyAktywny)
VALUES ('Piotr', 'Kasprzak', 'p123', 'piotr.kasprzak@example.com', 'Kasjer', 1)/
INSERT INTO Pracownik (imie, nazwisko, haslo, email, rolaPracownika, czyAktywny)
VALUES ('Karolina', 'Zielinska', 'admin1', 'karolina.z@example.com', 'Administrator', 1)/

INSERT INTO Produkt (nazwa, typProduktu, cena)
VALUES ('Nachos', 'Snacks', 5.00)/
INSERT INTO Produkt (nazwa, typProduktu, cena)
VALUES ('Water', 'Drinks', 5.00)/

INSERT INTO Zamowienie (dataZamowienia, status, metodaPlatnosci, klientID, pracownikID, kwotaLaczna)
VALUES (SYSTIMESTAMP, 'Paid', 'Card', 1, 1, 40.00)/
INSERT INTO Zamowienie (dataZamowienia, status, metodaPlatnosci, klientID, pracownikID, kwotaLaczna)
VALUES (SYSTIMESTAMP, 'Paid', 'Cash', 2, 1, 15.00)/

INSERT INTO PozycjaSprzedazy (ilosc, zamowienieID, produktID)
VALUES (1, 1, 1)/
INSERT INTO PozycjaSprzedazy (ilosc, zamowienieID, produktID)
VALUES (2, 1, 2)/

INSERT INTO Bilet (status, seansID, zamowienieID, rodzajeZnizekID)
VALUES ('Purchased', 1, 1, 1)/
INSERT INTO Bilet (status, seansID, zamowienieID, rodzajeZnizekID)
VALUES ('Purchased', 2, 2, 3)/


INSERT INTO Logi (dataCzas, inicjujacy, tresc)
VALUES (SYSTIMESTAMP, 'System', 'Added example records.')/
INSERT INTO Logi (dataCzas, inicjujacy, tresc)
VALUES (SYSTIMESTAMP, 'Administrator', 'Logging test.')/

