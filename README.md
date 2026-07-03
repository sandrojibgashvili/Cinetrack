# CineTrack

CineTrack არის Android აპლიკაცია ფილმებისა და სერიალების პირადი watchlist-ისთვის. მომხმარებელს შეუძლია დაამატოს ფილმი ან სერიალი, მიუთითოს ჟანრი, წელი, სტატუსი, რეიტინგი და favorite მონიშვნა.

## ფუნქციონალი

- ფილმებისა და სერიალების სია
- ახალი ჩანაწერის დამატება
- არსებული ჩანაწერის რედაქტირება
- ჩანაწერის წაშლა long press-ით
- favorite მონიშვნა ვარსკვლავით
- მენიუ ფილტრებით:
  - All
  - Movies
  - Series
  - Favorites
  - Watching
  - Watched
- სტატისტიკის ფანჯარა:
  - სულ რამდენი ჩანაწერია
  - რამდენია ფილმი
  - რამდენია სერიალი
  - რამდენია ნანახი
  - რამდენია ახლა საყურებელი
  - რამდენია favorite

## სავალდებულო მოთხოვნები

- **Menu** - გამოყენებულია Android options menu ფილტრებისთვის, სტატისტიკისთვის და About ფანჯრისთვის.
- **List** - გამოყენებულია `ListView` და custom adapter ფილმებისა და სერიალების საჩვენებლად.
- **MVVM არქიტექტურა** - პროექტი დაყოფილია model, repository, viewmodel და ui ფენებად.
- **ბაზასთან კავშირი** - გამოყენებულია local SQLite database `SQLiteOpenHelper`-ით.
- **ახალი ფუნქციონალი** - დამატებულია Statistics screen/dialog, რომელიც ითვლის watchlist-ის მონაცემებს.

## ტექნიკური დეტალები

- ენა: Kotlin
- UI: XML layouts + native Android Views
- Database: SQLite
- Architecture: MVVM
- Min SDK: 23
- Target SDK: 34
- Package name: `com.example.cinetrack`

## პროექტის სტრუქტურა

```text
app/src/main/kotlin/com/example/cinetrack/
├── data/
│   └── CineTrackDatabase.kt
├── model/
│   ├── MediaItem.kt
│   └── MediaStats.kt
├── repository/
│   └── MediaRepository.kt
├── ui/
│   ├── MainActivity.kt
│   └── MediaAdapter.kt
└── viewmodel/
    └── MediaViewModel.kt
```

## როგორ გავუშვათ

1. გახსენით პროექტი Android Studio-ში.
2. დაელოდეთ Gradle sync-ს.
3. აირჩიეთ emulator ან ფიზიკური Android მოწყობილობა.
4. დააჭირეთ Run ღილაკს.

აპლიკაცია პირველად გაშვებისას ბაზაში ამატებს რამდენიმე საწყის ჩანაწერს, რომ სია ცარიელი არ იყოს.
