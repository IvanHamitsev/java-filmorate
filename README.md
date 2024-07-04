# Filmorate
## Схема БД для хранения данных
  
![DB Scheme](db.png)  

### Примеры запросов к базе  
- Получение списка пользователей
```sql
SELECT first_name,  
       last_name,  
       login,  
       email  
FROM User  
ORDER BY last_name  
```
- Получение списка друзей пользователя `user_id = 1`
```sql
SELECT u.first_name,  
       u.last_name,  
       u.login,  
       u.email  
FROM User u 
INNER JOIN Friendship f 
ON ((f.user1_id = 1 AND f.user2_id = u.user_id)  
OR (f.user2_id = 1 AND f.user1_id = u.user_id))
```
  
- Получение списка "подтверждённых" друзей пользователя `user_id = 1`
```sql
SELECT u.first_name,  
       u.last_name,  
       u.login,  
       u.email  
FROM User u 
INNER JOIN Friendship f 
ON ((f.user1_id = 1 AND f.user2_id = u.user_id)  
OR (f.user2_id = 1 AND f.user1_id = u.user_id))
WHERE f.user1_authorized = true  
AND  f.user2_authorized = true  
```

- Полулучение списка 10 фильмов в жанре `комедия` с наибольшим число лайков
```sql
SELECT f.name,
       COUNT(DISTINCT l.user_id) AS likes
FROM Films f 
INNER JOIN Like l ON l.film_id = f.film_id
INNER JOIN Films_genre fg ON fg.film_id = f.film_id
INNER JOIN Genre g ON g.genre_id = fg.genre_id
WHERE g.name = 'комедия'
GROUP BY f.name
ORDER BY likes DESC
LIMIT 10
```

- Полулучение списка 10 фильмов с наибольшим число лайков, которые понравились пользователю `user_id = 1`
```sql
SELECT f.film_name,
       f.likes
FROM (
    SELECT f.name AS film_name,
           f.film_id AS id,
           COUNT(DISTINCT l.user_id) AS likes
    FROM Films f
    INNER JOIN Like l ON l.film_id = f.film_id
    GROUP BY f.id_film
) AS f
WHERE id IN (
    SELECT film_id
    FROM Films f
    INNER JOIN Like l ON l.film_id = f.film_id
    WHERE l.user_id = 1
)
ORDER BY likes DESC
LIMIT 10;
```