## Тестовое задание

### Запуск
    gradle test

### Структура

Первые два теста проверяют базовый функционал - получение токена и создание игрока
После идут два вложенных тестовых класса:

**testMethodGetOne**
Проверяем, что данные созданного пользователя отображаются верно при вызове метода POST /api/automationTask/getOne

**testCreation12Players**
Создаем 12 игроков, проверяем, что мы можем получить их данные методом GET /api/automationTask/getAll и удаляем их
