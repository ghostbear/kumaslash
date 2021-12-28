FROM node:16-alpine
# Copy the bot into container
COPY . /app
# Change directory
WORKDIR /app
# Install dependencies
RUN npm install
# Build the bot
RUN npm run build
CMD ["npm", "run", "serve"]