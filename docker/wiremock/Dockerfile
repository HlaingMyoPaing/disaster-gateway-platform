# Use the official WireMock image
FROM wiremock/wiremock:latest

# Create directories for mappings and files
RUN mkdir -p /home/wiremock/mappings /home/wiremock/__files

# Copy stubs and responses into the container
COPY mappings/ /home/wiremock/mappings/
COPY __files/ /home/wiremock/__files/

# Expose the correct internal port (WireMock listens on 8080)
EXPOSE 8081

# Start WireMock with correct root directory
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["--root-dir", "/home/wiremock", "--port", "8081"]