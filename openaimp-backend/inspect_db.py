import sqlite3
import os

db_path = r'd:\1-2026-projects\Open-Aerion-Integrated-Management-Platform\openaimp-backend\src\main\resources\little_navmap_navigraph.sqlite'

if not os.path.exists(db_path):
    print(f"Error: Database file not found at {db_path}")
    exit(1)

try:
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    
    # List all tables
    print("--- Tables ---")
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = cursor.fetchall()
    for table in tables:
        print(table[0])
        
    # Inspect relevant tables
    target_tables = ['airport', 'runway', 'approach', 'approach_leg', 'transition', 'waypoint', 'airway']
    
    for target in target_tables:
        print(f"\n--- Schema for {target} ---")
        try:
            cursor.execute(f"PRAGMA table_info({target})")
            columns = cursor.fetchall()
            if columns:
                for col in columns:
                    print(f"{col[1]} ({col[2]})")
            else:
                print("Table not found or empty schema")
        except Exception as e:
            print(f"Error inspecting {target}: {e}")

    conn.close()

except Exception as e:
    print(f"Database error: {e}")
