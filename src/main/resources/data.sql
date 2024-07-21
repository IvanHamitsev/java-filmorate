INSERT INTO rating (name)
VALUES 
              ('G'),
              ('PG'),
              ('PG-13'),
              ('R'),
              ('NC-17')
ON CONFLICT (name) DO NOTHING;