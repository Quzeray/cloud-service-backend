# [Дипломная работа “Облачное хранилище”](https://github.com/netology-code/jd-homeworks/blob/master/diploma/cloudservice.md)

## Описание проекта
REST-сервис предоставляет интерфейс для загрузки файлов и вывода списка уже загруженных файлов пользователя, с возможностью дополнительных операций в соответствии с заранее описанной спецификацией. Все запросы к сервису должны быть авторизованы. Авторизация происходит через JWT. Заранее подготовленное веб-приложение (FRONT) может подключаться к разработанному сервису без доработок, а также использовать функционал FRONT для авторизации, загрузки и вывода списка файлов пользователя. Клиент имеет доступ к операциям загрузки файлов и просмотра списка файлов, а также, возможно, к другим функциональным возможностям в соответствии с предоставленной спецификацией.

[Файл Спецификации](https://github.com/netology-code/jd-homeworks/blob/master/diploma/CloudServiceSpecification.yaml)

## Используемые технологии
### Разработка
- Spring framework
- JJWT
- Lombok
- PotgresSQL
- Docker
### Тестирование
- JUnit 
- Mockito
- Testcontainers

## Заппуск приложения
1. Скачать проект
2. Выполнить gradle build
3. Выполнить docker-compose build
4. Выполнить docker-compose up

## Авторизационные данные
user:
- login - user
- password - password

admin:
- login - admin
- password - password



 