FROM node:18-alpine

# Tạo thư mục app
WORKDIR /app

# Copy file cấu hình và cài dependencies
COPY package*.json ./
RUN npm install

# Copy source code
COPY . .

# Expose port mặc định Vue dev server
EXPOSE 5173

# Chạy dev server
CMD ["npm", "run", "dev", "--", "--host"]

