# Search the docs

## Słownik domenowy
- użytkownik - osoba zalogowana do aplikacji (aplikacja dostępna jedynie dla zalogowanych); posiada pokoje, które posiadają pliki
- plik/dokument - plik w formacie PDF zawierający tekst
- pokój - wirtualny folder, do którego można dodawać dokumenty -> zbiór plików

## Wymagania funkcjonalne

### Dokumenty
 - użytkownik może wrzucać pliki, które zostaną zaindeksowane
 - użytkownik domyślnie posiada jeden pokój prywatny, do którego jedynie on ma dostęp (My documents)
 - użytkownik może tworzyć pokoje publiczne i prywatne
 - użytkownik może dodawać innych użytkowników do swoich pokojów publicznych (za pomocą kodu dostępu)
 - użytkownik może nadać prawa do edycji pokoju (dodawanie i usuwanie plików) lub jedynie przeglądania plików
 - użytkownik może usuwać uprzednio dodanych uczestników z pokoju, wówczas tracą oni do niego dostęp
 - użytkownik może usunąć pokój (usunięte zostają pliki)
 - właściciel pokoju może zawsze dodawać i usuwać pliki z danego pokoju
 - użytkonicy z prawem edycji pokoju mogą dodawać i usuwać pliki z danego pokoju

### Zarządzanie kontem
 - użytkownik może założyć konto w systemie (za pomocą Google Auth lub ręcznie)
 - 