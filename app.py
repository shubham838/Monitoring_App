import psutil
from flask import Flask

app = Flask(__name__)

@app.route("/")
def index():
    cpu_percent = psutil.cpu_percent()
    mem_percent = psutil.virtual_memory().percent
    Message = None
    if cpu_percent > 80 or mem_percent > 80:
        Message = "High CPU/MEM"
    return f"CPU: {cpu_percent} and Mem:{mem_percent}"

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
