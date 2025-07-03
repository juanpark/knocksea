import os
import pandas as pd
from pymongo import MongoClient
from dotenv import load_dotenv

def main():
    # .env 파일 경로 지정 및 로드
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.abspath(os.path.join(script_dir, '..'))
    dotenv_path = os.path.join(project_root, '.env')
    load_dotenv(dotenv_path)

    # 환경변수 읽기
    username = os.getenv("MONGODB_USERNAME")
    password = os.getenv("MONGODB_PASSWORD")
    host = os.getenv("MONGODB_HOST")
    port = os.getenv("MONGODB_PORT")
    database = os.getenv("MONGODB_DATABASE")

    # MongoDB URI 생성 및 클라이언트 연결
    uri = f"mongodb://{username}:{password}@{host}:{port}"
    client = MongoClient(uri)
    db = client[database]
    collection = db["fishing_spot"]

    # CSV 파일 경로
    csv_path = os.path.join(project_root, 'sql', 'mongodb', 'fishing_spot.csv')

    # CSV 읽기 및 데이터 삽입
    try:
        df = pd.read_csv(csv_path)
    except FileNotFoundError:
        print(f"CSV 파일을 찾을 수 없습니다: {csv_path}")
        return
    except Exception as e:
        print(f"CSV 파일 읽기 중 오류 발생: {e}")
        return

    data = df.to_dict(orient="records")
    if not data:
        print("CSV에 데이터가 없습니다.")
        return

    try:
        result = collection.insert_many(data)
        print(f"{len(result.inserted_ids)}개의 문서를 MongoDB에 삽입했습니다.")
    except Exception as e:
        print(f"MongoDB 삽입 중 오류 발생: {e}")

if __name__ == "__main__":
    main()