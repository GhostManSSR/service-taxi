#Taxi Microservices Platform

Микросервисное backend-приложение для моделирования сервиса заказа такси.
Проект демонстрирует архитектуру микросервисов, работу с базой данных, очередями сообщений и многопоточную обработку.

---

## Основные возможности

* Регистрация пассажиров и водителей
* Создание и управление поездками
* Автоматическое назначение водителя
* Асинхронная обработка уведомлений
* Многопоточная обработка задач (worker pool)
* Защита от race condition
* Swagger-документация API

---

## Архитектура

Проект состоит из трёх микросервисов:

### 1. User Service

* Управление пассажирами и водителями
* Назначение свободного водителя (с блокировками)

### 2. Trip Service

* Создание поездок
* Назначение водителя
* Изменение статусов поездки
* Отправка событий в очередь

### 3. Notification Service

* Обработка уведомлений в фоне
* Пул воркеров (многопоточность)
* Retry логика (до 3 попыток)

---

## Технологии

* Java 21
* Spring Boot
* Spring Data JPA
* PostgreSQL
* Redis
* RabbitMQ
* Docker Compose
* Swagger (OpenAPI)

---

## База данных

Используется PostgreSQL с одной БД и разными схемами:

* `user_service`
* `trip_service`
* `notification_service`

---

## Запуск проекта

### 1. Клонировать репозиторий

```bash
git clone https://github.com/your-repo/taxi-app.git
cd taxi-app
```

---

### 2. Запустить инфраструктуру

```bash
docker-compose up -d
```

Это поднимет:

* PostgreSQL
* Redis
* RabbitMQ

---

### 3. Создать схемы в БД

Подключись к PostgreSQL и выполни:

```sql
CREATE SCHEMA user_service;
CREATE SCHEMA trip_service;
CREATE SCHEMA notification_service;
```

---

### 4. Запустить сервисы

Каждый сервис запускается отдельно:

```bash
./gradlew :user-service:run
```

```bash
./gradlew :trip-service:run
```

```bash
./gradlew :notification-service:run
```

---

## API (Swagger)

После запуска:

* User Service
  http://localhost:8081/swagger-ui/index.html

* Trip Service
  http://localhost:8082/swagger-ui/index.html

* Notification Service
  http://localhost:8083/swagger-ui/index.html

---

## Основной сценарий работы

1. Создание пассажира
2. Регистрация водителя
3. Создание поездки
4. Автоматическое назначение водителя
5. Отправка события в очередь
6. Notification Service обрабатывает уведомление


## 👨‍💻 Автор

Проект выполнен в рамках изучения микросервисной архитектуры и backend-разработки.

---
