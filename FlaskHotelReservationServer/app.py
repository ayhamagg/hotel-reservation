from flask import Flask, request, jsonify
from flask_cors import CORS
import pymysql
import logging

app = Flask(__name__)
CORS(app)
logging.basicConfig(level=logging.DEBUG)

# Database connection
def get_db_connection():
    return pymysql.connect(
        host='localhost',
        user='root',
        password='',
        db='hotelreservation',
        cursorclass=pymysql.cursors.DictCursor
    )

@app.route('/reservations', methods=['POST'])
def add_reservation():
    data = request.json
    if not data:
        return jsonify({'error': 'Invalid input'}), 400

    full_name = data.get('fullName')
    email = data.get('email')
    phone = data.get('phone')
    room_type = data.get('roomType')
    room_price = data.get('roomPrice')
    check_in_date = data.get('checkInDate')
    check_out_date = data.get('checkOutDate')
    num_guests = data.get('numGuests')
    num_rooms = data.get('numRooms')
    total_price = data.get('totalPrice')

    if not all([full_name, email, phone, room_type, room_price, check_in_date, check_out_date, num_guests, num_rooms, total_price]):
        return jsonify({'error': 'Missing required fields'}), 400

    connection = get_db_connection()
    try:
        with connection.cursor() as cursor:
            sql = """
            INSERT INTO reservations (full_name, email, phone, room_type, room_price, check_in_date, check_out_date, num_guests, num_rooms, total_price)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            cursor.execute(sql, (full_name, email, phone, room_type, room_price, check_in_date, check_out_date, num_guests, num_rooms, total_price))
            connection.commit()
        return jsonify({'message': 'Reservation added successfully'}), 201
    except Exception as e:
        logging.error(f"Error adding reservation: {e}")
        return jsonify({'error': 'Failed to add reservation'}), 500
    finally:
        connection.close()

if __name__ == '__main__':
    app.run(host='192.168.148.62', port=8081, debug=True)
