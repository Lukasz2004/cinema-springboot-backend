CREATE TABLE GatunekFilmu (
    gatunekID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nazwa VARCHAR2(25) UNIQUE NOT NULL

)/
CREATE TABLE Rezyser (
    rezyserID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    imieLubPseudonim VARCHAR2(50) NOT NULL,
    nazwisko VARCHAR2(70),
    krajPochodzenia VARCHAR2(60),
    dataUrodzenia DATE
)/
CREATE TABLE Aktor (
    aktorID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    imieLubPseudonim VARCHAR2(50) NOT NULL,
    nazwisko VARCHAR2(70),
    krajPochodzenia VARCHAR2(60),
    dataUrodzenia DATE
)/
CREATE TABLE Film (
    filmID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tytul VARCHAR2(100) UNIQUE NOT NULL,
    gatunekID INTEGER REFERENCES GatunekFilmu(gatunekID) NOT NULL,
    czasTrwania INTEGER NOT NULL,
    opis LONG,
    rezyserID INTEGER REFERENCES Rezyser(rezyserID) NOT NULL,
    zalecenieWiekowe VARCHAR2(30),
    dataPremiery DATE,
    jezykOryginalny VARCHAR2(50),
    CONSTRAINT chk_Film_czasTrwaniaPoprawny CHECK (czasTrwania >= 0)
)/
CREATE TABLE FilmAktor (
    aktorID INTEGER REFERENCES Aktor(aktorID) NOT NULL,
    filmID INTEGER REFERENCES Film(filmID) NOT NULL
)/
CREATE TABLE TypSeansu (
    typSeansuID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nazwa VARCHAR2(20) UNIQUE NOT NULL,
    cenaStandardowa NUMERIC(10,2) NOT NULL,
                           CONSTRAINT chk_TypSeansu_cenaPoprawna CHECK (cenaStandardowa >= 0)
)/
CREATE TABLE Sala (
    salaID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nazwa VARCHAR2(20) UNIQUE NOT NULL,
    pojemnosc INTEGER NOT NULL,
    CONSTRAINT chk_Sala_pojemnoscPoprawna CHECK (pojemnosc >= 0)
)/
CREATE TABLE Seans (
    seansID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    dataCzas TIMESTAMP WITH TIME ZONE NOT NULL,
    filmID INTEGER REFERENCES Film(filmID) NOT NULL,
    salaID INTEGER REFERENCES Sala(salaID) NOT NULL,
    typSeansuID INTEGER REFERENCES typSeansu(typSeansuID) NOT NULL,
    jezykSeansu VARCHAR2(50) NOT NULL,
    dubbingCzyNapisy VARCHAR2(9),
    cenaSpecjalna NUMBER(10,2),
    CONSTRAINT chk_Seans_dubbingCzyNapisy CHECK (dubbingCzyNapisy IN ('DUBBING', 'SUBTITLES')),
    CONSTRAINT chk_Seans_cenaDodatnia CHECK (cenaSpecjalna >= 0)
)/
CREATE TABLE RodzajeZnizek (
    znizkaID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nazwa VARCHAR2(25) UNIQUE NOT NULL,
    procentZnizki NUMERIC(10,2) NOT NULL,
    CONSTRAINT chk_RodzajeZnizek_poprawnyProcent CHECK (procentZnizki >=0 AND procentZnizki <=100)
)/
CREATE TABLE Klient (
    klientID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    imie VARCHAR2(40) NOT NULL,
    nazwisko VARCHAR2(70) NOT NULL,
    haslo VARCHAR2(100) NOT NULL,
    email VARCHAR2(255) UNIQUE NOT NULL,
    poziomLojalnosciowy INTEGER NOT NULL,
    CONSTRAINT chk_Klient_emailPoprawny CHECK (email LIKE '%_@_%._%')
)/
CREATE TABLE Pracownik (
    pracownikID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    imie VARCHAR2(40) NOT NULL,
    nazwisko VARCHAR2(70) NOT NULL,
    haslo VARCHAR2(100) NOT NULL,
    email VARCHAR2(255) UNIQUE NOT NULL,
    rolaPracownika VARCHAR2(13) NOT NULL,
    czyAktywny NUMBER(1) NOT NULL,
    CONSTRAINT chk_Pracownik_rolaPoprawna CHECK (rolaPracownika  IN ('Kasjer','Menadżer','Administrator')),
    CONSTRAINT chk_Pracownik_emailPoprawny CHECK (email LIKE '%_@_%._%')
)/
CREATE TABLE Produkt (
    produktID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nazwa VARCHAR2(50) NOT NULL UNIQUE,
    typProduktu VARCHAR2(20) NOT NULL,
    cena NUMBER(10,2) NOT NULL,
    CONSTRAINT chk_Produkt_cenaPoprawna CHECK (cena >= 0)
)/
CREATE TABLE Zamowienie (
    zamowienieID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    dataZamowienia TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR2(30) NOT NULL,
    metodaPlatnosci VARCHAR2(8) NOT NULL,
    klientID INTEGER REFERENCES Klient(klientID),
    pracownikID INTEGER REFERENCES Pracownik(pracownikID),
    kwotaLaczna NUMBER(10,2),
    CONSTRAINT chk_Zamowienie_platnosc CHECK (metodaPlatnosci IN ('Cash','Card','Transfer', 'Counter')),
    CONSTRAINT chk_Zamowienie_kwotaPoprawna CHECK (kwotaLaczna>=0)
)/
CREATE TABLE PozycjaSprzedazy (
    pozycjaID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ilosc INTEGER NOT NULL,
    zamowienieID INTEGER REFERENCES Zamowienie(zamowienieID) NOT NULL,
    produktID INTEGER REFERENCES Produkt(produktID) NOT NULL,
    CONSTRAINT chk_Pozycja_iloscPoprawna CHECK (ilosc>0)
)/
CREATE TABLE Bilet (
    biletID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    status VARCHAR2(30) NOT NULL,
    seansID INTEGER REFERENCES Seans(seansID) NOT NULL,
    zamowienieID INTEGER REFERENCES Zamowienie(zamowienieID) NOT NULL,
    rodzajeZnizekID INTEGER REFERENCES RodzajeZnizek(znizkaID)
)/
CREATE TABLE Logi (
    logID INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    dataCzas TIMESTAMP WITH TIME ZONE NOT NULL,
    inicjujacy VARCHAR2(255),
    tresc VARCHAR2(1000) NOT NULL
)/



--FUNKCJE
CREATE OR REPLACE FUNCTION liczbaWolnychMiejsc(p_seansID NUMBER)
RETURN NUMBER
AS
    v_pojemnosc NUMBER;
    v_zajete NUMBER;
BEGIN
SELECT s.pojemnosc
INTO v_pojemnosc
FROM Sala s
         JOIN Seans se ON se.salaID = s.salaID
WHERE se.seansID = p_seansID;

SELECT COUNT(*)
INTO v_zajete
FROM Bilet
WHERE seansID = p_seansID AND status = 'Zakupiony';

RETURN v_pojemnosc - v_zajete;
END;
/



CREATE OR REPLACE FUNCTION czySeansDostepny(p_seansID NUMBER)
RETURN VARCHAR2
AS
    v_wolne NUMBER;
BEGIN
    v_wolne := liczbaWolnychMiejsc(p_seansID);
RETURN CASE WHEN v_wolne > 0 THEN 'TRUE' ELSE 'FALSE' END;
END;
/



CREATE OR REPLACE FUNCTION nazwaFilmuSeansu(p_seansID NUMBER)
RETURN VARCHAR2
AS
    v_tytul Film.tytul%TYPE;
BEGIN
SELECT f.tytul
INTO v_tytul
FROM Film f
         JOIN Seans s ON s.filmID = f.filmID
WHERE s.seansID = p_seansID;

RETURN v_tytul;
END;
/



CREATE OR REPLACE FUNCTION gdzieJestSeans(p_biletID NUMBER)
RETURN VARCHAR2
AS
    v_sala VARCHAR2(255);
BEGIN
SELECT sa.nazwa
INTO v_sala
FROM Sala sa
         JOIN Seans se ON se.salaID = sa.salaID
         JOIN Bilet b ON b.seansID = se.seansID
WHERE b.biletID = p_biletID;

RETURN v_sala;
END;
/



CREATE OR REPLACE FUNCTION sumaKupionychBiletow(p_klientID NUMBER)
RETURN NUMBER
AS
    v_suma NUMBER;
BEGIN
SELECT COUNT(*)
INTO v_suma
FROM Bilet b
         JOIN Zamowienie z ON b.zamowienieID = z.zamowienieID
WHERE z.klientID = p_klientID
  AND b.status = 'Zakupiony';

RETURN v_suma;
END;
/


--PROCEDURY
CREATE OR REPLACE PROCEDURE dodajBiletDoZamowienia(
    p_zamowienieID NUMBER,
    p_seansID      NUMBER,
    p_znizkaID     NUMBER DEFAULT NULL,
    p_biletID      OUT NUMBER
)
AS
    v_wolne NUMBER;
BEGIN
    v_wolne := liczbaWolnychMiejsc(p_seansID);

    IF v_wolne <= 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Brak wolnych miejsc na seansie.');
END IF;

INSERT INTO Bilet(status, seansID, zamowienieID, rodzajeZnizekID)
VALUES ('Zakupiony', p_seansID, p_zamowienieID, p_znizkaID)
    RETURNING biletID INTO p_biletID;

END;
/



CREATE OR REPLACE PROCEDURE anulujZamowienie(p_zamowienieID NUMBER)
AS
BEGIN
UPDATE Zamowienie
SET status = 'Anulowane'
WHERE zamowienieID = p_zamowienieID;

UPDATE Bilet
SET status = 'Anulowany'
WHERE zamowienieID = p_zamowienieID;
END;
/



CREATE OR REPLACE PROCEDURE utworzSeans(
    p_filmID       NUMBER,
    p_salaID       NUMBER,
    p_typSeansuID  NUMBER,
    p_data         DATE,
    p_jezykSeansu  VARCHAR2,
    p_dubbCzyNap   VARCHAR2
)
AS
    v_kolizje NUMBER;
BEGIN
SELECT COUNT(*)
INTO v_kolizje
FROM Seans
WHERE salaID = p_salaID
  AND ABS((p_data - CAST(dataCzas AS DATE)) * 24 * 60) < 120; -- 2h okna kolizji

IF v_kolizje > 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Kolizja czasowa w sali.');
END IF;

INSERT INTO Seans(dataCzas, filmID, salaID, typSeansuID,
                  jezykSeansu, dubbingCzyNapisy)
VALUES (p_data, p_filmID, p_salaID,
        p_typSeansuID, p_jezykSeansu, p_dubbCzyNap);
END;
/



CREATE OR REPLACE PROCEDURE dodajProduktDoSprzedazy(
    p_zamowienieID NUMBER,
    p_produktID    NUMBER,
    p_ilosc        NUMBER
)
AS
BEGIN
INSERT INTO PozycjaSprzedazy(zamowienieID, produktID, ilosc)
VALUES (p_zamowienieID, p_produktID, p_ilosc);
END;
/



CREATE OR REPLACE PROCEDURE utworzZamowienie(
    p_pracownikID      NUMBER DEFAULT NULL,
    p_klientID         NUMBER DEFAULT NULL,
    p_metodaPlatnosci  VARCHAR2,
    p_status           VARCHAR2,
    p_zamowienieID     OUT NUMBER
)
AS
BEGIN
INSERT INTO Zamowienie(metodaPlatnosci, pracownikID, klientID,
                       kwotaLaczna, status, DATAZAMOWIENIA)
VALUES (p_metodaPlatnosci, p_pracownikID, p_klientID,
        0, p_status, SYSDATE)
    RETURNING zamowienieID INTO p_zamowienieID;
END;
/

--WIDOKI
CREATE OR REPLACE VIEW v_AktywneFilmy AS
SELECT f.*
FROM Film f
WHERE EXISTS (
    SELECT 1
    FROM Seans s
    WHERE s.filmID = f.filmID
      AND s.dataCzas > SYSDATE
)/


CREATE OR REPLACE VIEW v_AktywneSeanse AS
SELECT
    s.seansID,
    f.filmID,
    f.tytul,
    s.dataCzas,
    ts.nazwa AS typSeansu,
    s.JEZYKSEANSU,
    s.dubbingCzyNapisy,
    sa.nazwa AS sala,
    liczbaWolnychMiejsc(s.seansID) AS wolneMiejsca
FROM Seans s
         JOIN Film f ON s.filmID = f.filmID
         JOIN Sala sa ON sa.salaID = s.salaID
         JOIN TypSeansu ts ON ts.typSeansuID = s.typSeansuID
WHERE s.dataCzas > SYSDATE/


CREATE OR REPLACE VIEW v_SeanseZFilmami AS
SELECT
    s.seansID,
    f.tytul,
    s.jezykSeansu,
    s.dubbingCzyNapisy,
    t.nazwa AS typSeansu,
    s.dataCzas,
    sa.salaID AS sala,
    t.cenaStandardowa AS cena,
    liczbaWolnychMiejsc(s.seansID) AS wolneMiejsca
FROM Seans s
         JOIN Film f ON s.filmID = f.filmID
         JOIN Sala sa ON sa.salaID = s.salaID
         JOIN TypSeansu t ON t.typSeansuID = s.typSeansuID/



CREATE OR REPLACE VIEW v_RezerwacjeKlienta AS
SELECT
    z.zamowienieID,
    z.dataZamowienia,
    z.kwotaLaczna,
    b.biletID,
    b.status AS statusBiletu,
    rz.procentZnizki,
    s.dataCzas,
    s.salaID,
    f.tytul
FROM Zamowienie z
         JOIN Bilet b ON b.zamowienieID = z.zamowienieID
         JOIN Seans s ON s.seansID = b.seansID
         JOIN Film f ON f.filmID = s.filmID
         LEFT JOIN RodzajeZnizek rz ON rz.znizkaID = b.rodzajeZnizekID
WHERE z.status <> 'Anulowane'/



CREATE OR REPLACE VIEW v_BiletyZamowienia AS
SELECT
    b.biletID,
    b.status,
    f.tytul,
    s.dataCzas,
    s.salaID,
    rz.procentZnizki
FROM Bilet b
         JOIN Seans s ON s.seansID = b.seansID
         JOIN Film f ON f.filmID = s.filmID
         LEFT JOIN RodzajeZnizek rz ON rz.znizkaID = b.rodzajeZnizekID/



CREATE OR REPLACE VIEW v_PokazBilet AS
SELECT
    b.biletID,
    b.seansID,
    b.status,
    s.dataCzas,
    s.salaID
FROM Bilet b
         JOIN Seans s ON s.seansID = b.seansID/

