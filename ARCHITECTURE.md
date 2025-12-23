# Архитектура проекта LiJo

Приложение построено по принципам **Clean Architecture** и **MVVM** (Model-View-ViewModel).

## Слой данных (Data)
* **Room Database**: Единственный источник истины.
* **Entities**: `TaskList` (списки) и `ListItem` (задачи). Связаны через `ForeignKey`.
* **DAOs**:
    - `TaskListDao`: Получение списков с агрегированным подсчетом задач (`TaskListWithCount`).
    - `ListItemDao`: Сложная логика сортировки задач (активные выше завершенных).
* **Repository**: `TaskRepository` — изолирует логику базы данных от UI-слоя.

## Слой представления (UI)
* **Navigation**: Один `NavHost` в `MainActivity`. Маршруты: `main`, `details/{listId}`, `settings`.
* **ViewModel**: Хранит состояние экрана (`StateFlow`) и обрабатывает события пользователя.
* **Compose UI**: Чистые функции отрисовки. Компоненты разделены на экраны (Screens) и переиспользуемые элементы (Components).

## Управление темами
Используется `ThemeManager` на базе **DataStore**, который позволяет сохранять выбор пользователя (темная/светлая тема) между запусками приложения.