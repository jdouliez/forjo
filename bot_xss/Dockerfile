FROM python:3

COPY geckodriver /usr/bin/geckodriver
COPY xss_bot.py /app/bot.py
COPY requirements.txt /tmp/requirements.txt

RUN pip3 install --upgrade pip
RUN pip3 install -r /tmp/requirements.txt
RUN chmod 777 /usr/bin/geckodriver

RUN apt-get update && apt-get install -y firefox-esr

ENTRYPOINT python -u /app/bot.py "http://frontend:4200"
