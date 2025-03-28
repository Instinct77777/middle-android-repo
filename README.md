# AndroidPracticum
Продвинутая разработка UI. Jetpack Compose. Кастомные View. Анимации.

Sprint_1

Упростил код, понял что грузить viewmodel корзину и лишние элементы ни к чему. Все лишние элементы туда же.

Все только по ТЗ.

Оставил только Background Image.

XML (legacy)
Проверка в addView() выбрасывает IllegalStateException при попытке добавить больше 2 детей
Константа MAX_CHILDREN = 2 обеспечивает контроль

gravity = Gravity.CENTER в init и расчет centerX в onLayout обеспечивают центр

Первый child: centerY - offset (верх)

Второй child: centerY + offset (низ)

Используется (для того чтобы картинки красиво разьезжались, я в легаси использовал картинки вместо текста для красоты) INITIAL_OFFSET_FACTOR = 0.5f для начального смещения

Проверка размеров view перед анимацией

Установка начальной видимости INVISIBLE при добавлении view

Длительности анимации вынесены в параметры конструктора

Сохранены оригинальные значения по умолчанию

Добавлен AccelerateDecelerateInterpolator для плавности анимации

Оптимизированный onMeasure:

Более точные расчеты размеров дочерних элементов (ты писал замечание в 1 ревью вроде)

Compose страница

Максимум 2 дочерних элемента

Проверка if (children.count { it != null } > 2) выбрасывает IllegalStateException, если больше двух.

Используются nullable Composable-параметры (firstChild, secondChild), что позволяет гибко управлять содержимым.

Горизонтальное центрирование

BoxWithConstraints с модификатором fillMaxSize() занимает всю  область.

Дочерние Box используют Alignment.Center, что обеспечивает центрирование по горизонтали.

Вертикальное размещение

Первый : translationY = -maxOffset * offset (движение вверх).

Второй : translationY = maxOffset * offset (движение вниз).

Начальная позиция — центр  (Alignment.Center).

Анимации

Прозрачность: alphaAnimation меняется от 0f до 1f с длительностью  (по умолчанию 2000 мс).

Перемещение: offsetAnimation длится durationOffsetMillis (5000 мс, как в требованиях).

Эффекты запускаются одновременно в LaunchedEffect.

Гибкость

Параметры durationAlphaMillis и durationOffsetMillis можно переопределять.

Поддержка кастомного содержимого через лямбды firstChild/secondChild.

Вроде все по красоте. Поставь зачет пожалуйста:)


