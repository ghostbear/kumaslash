FROM node:16-alpine
# Copy the bot into container
COPY . /app
# Change directory
WORKDIR /app
# Shouldn't be included but removed either way user should create this file
RUN rm .env
# Install dependencies
RUN npm install
# Build the bot
RUN npm run build
CMD ["npm", "run", "serve"]