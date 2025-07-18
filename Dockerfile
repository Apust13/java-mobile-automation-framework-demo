FROM openjdk:17-slim

# 👇 Устанавливаем bash — иначе setup-скрипт Node.js не выполнится
RUN apt-get update && \
    apt-get install -y bash curl unzip wget git

# 👇 Установка Node.js 20 (теперь работает, потому что есть bash)
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs

# 👇 Установка Appium CLI
RUN npm install -g appium

# 👇 Установка Gradle вручную
ARG GRADLE_VERSION=8.13
RUN wget https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -P /opt && \
    unzip /opt/gradle-${GRADLE_VERSION}-bin.zip -d /opt && \
    ln -s /opt/gradle-${GRADLE_VERSION} /opt/gradle

ENV PATH="$PATH:/opt/gradle/bin"

WORKDIR /app
COPY . /app

ENTRYPOINT ["gradle", "05_testAndroidParallel"]
