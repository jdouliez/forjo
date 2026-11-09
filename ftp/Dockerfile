FROM fauria/vsftpd

COPY vsftpd.conf /etc/vsftpd/vsftpd.conf

EXPOSE 21 4559-4564

VOLUME /ftp/ftp/

CMD /usr/sbin/vsftpd /etc/vsftpd/vsftpd.conf && tail -f /dev/null