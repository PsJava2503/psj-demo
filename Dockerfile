FROM eclipse-temurin:17-jre

ARG MODULE

WORKDIR /app

COPY ${MODULE}/target/ /tmp/app-target/

RUN set -eu; \
    app_jar=""; \
    for candidate in /tmp/app-target/${MODULE}-*.jar; do \
      case "$candidate" in \
        *sources*|*javadoc*) continue ;; \
        *) app_jar="$candidate"; break ;; \
      esac; \
    done; \
    test -n "$app_jar"; \
    cp "$app_jar" /app/app.jar; \
    rm -rf /tmp/app-target

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
