AI-Powered Task Management System

This project is a Spring Boot-based AI-powered task management system. The application is containerized using Docker and deployed on Render.

Deployment Instructions on Render

Follow these steps to deploy updates to the application on Render using Docker.

1. Build the Updated Docker Image

Ensure that the application is updated and the Docker image is built with the appropriate platform (Render requires linux/amd64).

# Build the Docker image targeting the correct platform
docker build --platform linux/amd64 -t <your-dockerhub-username>/task-manager-service:latest .

2. Push the Image to Docker Hub

Push the updated Docker image to Docker Hub (or another container registry like GitHub Container Registry).

# Log in to Docker Hub
docker login

# Push the image to Docker Hub
docker push <your-dockerhub-username>/task-manager-service:latest

3. Deploy the Updated Image on Render

Log in to your Render Dashboard.

Navigate to your Web Service for the task manager.

Go to the Settings tab.

Update the Docker Image field to the latest image:

<your-dockerhub-username>/task-manager-service:latest

Click Save Changes.

Go to the Deploys tab and click Manual Deploy to trigger a deployment with the latest image.

4. Verify the Deployment

Once the deployment is complete:

Open the service URL provided by Render.

Check the Logs tab to confirm that the application started without errors.

Test the API endpoints using tools like Postman or cURL to ensure the updates are functional.

Environment Variables

Environment variables are managed securely in Render and should not be hardcoded in the application or Dockerfile.

Setting Environment Variables on Render

Go to the Settings tab of your Web Service.

Scroll to the Environment section.

Add or update the following variables:

SPRING_DATASOURCE_URL: The JDBC URL for your database.

SPRING_DATASOURCE_USERNAME: The username for your database.

SPRING_DATASOURCE_PASSWORD: The password for your database.

JWT_SECRET: Your secret key for JWT.

Save the changes and redeploy the service if necessary.

Testing Locally

To test the application locally, you can run it using Docker:

# Run the Docker container locally
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://<db-host>:3306/<db-name> \
  -e SPRING_DATASOURCE_USERNAME=<db-username> \
  -e SPRING_DATASOURCE_PASSWORD=<db-password> \
  -e JWT_SECRET=your-secret-key \
  <your-dockerhub-username>/task-manager-service:latest

Access the application at:

http://localhost:8080

Notes

Ensure you always build the Docker image with the --platform linux/amd64 flag to maintain compatibility with Render.

Regularly check the Render logs and monitor your database connection settings to avoid downtime.

Use Render's Deploy Hooks for CI/CD integration to streamline deployments.
