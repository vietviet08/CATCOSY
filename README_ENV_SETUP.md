# Environment Setup for CATCOSY Project

This document explains how to set up environment variables for the CATCOSY project, particularly for sensitive data like API keys.

## OpenAI API Configuration

The project uses OpenAI API for the chatbot functionality. To protect API keys and other sensitive information, follow these steps:

### Setting Up Local Development Environment

1. **Create a local .env file**

   Create a file named `.env` in the project root directory with the following content:

   ```
   OPENAI_API_KEY=your-actual-api-key-here
   OPENAI_MODEL=gpt-3.5-turbo
   OPENAI_MAX_TOKENS=300
   OPENAI_TIMEOUT=30
   ```

   Replace `your-actual-api-key-here` with your actual OpenAI API key.

2. **Keep your .env file secure**

   The `.env` file is added to `.gitignore` so it won't be committed to the repository. 
   NEVER commit this file to the repository as it contains sensitive information.

### Setting Up Production Environment

For production deployment, set the environment variables directly in your production environment:

- For Docker: Add the variables in your docker-compose.yml or Dockerfile
- For Kubernetes: Use secrets and environment variables
- For traditional hosting: Set the environment variables in your server configuration

## How It Works

The application will load environment variables in the following order of precedence:

1. System environment variables
2. Variables from .env file
3. Default values from application.properties

## For New Developers

When you clone this repository for the first time:

1. Copy the `application.properties.example` file to create your own `application.properties`
2. Create a `.env` file in the project root with your API keys
3. Run the application, and it will automatically load the variables from your .env file

## Security Best Practices

- Never commit API keys to the repository
- Regularly rotate your API keys
- Use different API keys for development and production
- Limit the permissions of your API keys when possible

If you have any questions about environment setup, contact the project maintainer.