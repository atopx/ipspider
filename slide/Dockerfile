# base image
FROM openjdk:8-jre
# set charset
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8 PYTHONUNBUFFERED=1
# set timezone
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Replace mirror source
COPY ./debian9-source /etc/apt/sources.list

# 安装chrome相关依赖
RUN apt-get update
RUN apt-get -y install apt-utils
RUN apt-get -y install netcat
RUN apt-get -y install libpango1.0-0
RUN apt-get -y install libxss1
RUN apt-get -y install fonts-liberation
RUN apt-get -y install libappindicator1
RUN apt-get -y install xdg-utils
RUN apt-get -y install libasound2
RUN apt-get -y install libgconf-2-4
RUN apt-get -y install libappindicator3-1
RUN apt-get -y install libatk-bridge2.0-0
RUN apt-get -y install libatspi2.0-0
RUN apt-get -y install libgtk-3-0
RUN apt-get -y install libx11-xcb1
RUN apt-get -y install libxtst6
RUN apt-get -y install libxtst6
RUN apt-get -y install libnspr4
RUN apt-get -y install libnss3
RUN apt-get clean

# set working directory
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# install chrome
COPY ./chromedriver /usr/src/chromedriver
RUN chmod +x /usr/src/chromedriver

COPY ./google-chrome-79.0.3945.deb /usr/src/google-chrome-79.0.3945.deb
RUN dpkg -i /usr/src/google-chrome-79.0.3945.deb

# add app
COPY ./slide.jar /usr/src/app/slide.jar

# run server
CMD ["java", "-jar", "slide.jar", "--spring.profiles.active=prod"]