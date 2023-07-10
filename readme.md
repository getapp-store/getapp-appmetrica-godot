# Godot AppMetrica

Плагин AppMetrica для игр на Godot

## Установка

- Cобрать модуль `app-metrica` командой `./gradlew build`
- Скопировать файлы `app-metrica-release.aar` и `AppMetrica.gdap` в папку `res://android/plugins`
- Вклчючить плагин в настройках экспорта проекта

![plugins](https://kovardin.ru/img/godot/mytracker/08.png)

## Инициализация

Пример загрузки и использования плагина

```GDScript
var metrica: Object

func _ready():
	if Engine.has_singleton("AppMetrica"):
		metrica = Engine.get_singleton("AppMetrica")
		
		metrica.config("xxxxxxxxxx")
		metrica.withLogs()
		metrica.init()
```