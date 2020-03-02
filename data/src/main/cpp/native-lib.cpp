#ifndef MovieController_hpp
#define MovieController_hpp

#include <jni.h>
#include <string>
#include <vector>
#include <map>

namespace movies {
    class Actor {
    public:
        std::string name;
        int age;

        //optional challenge 1: Load image from URL
        std::string imageUrl;
    };

    class Movie {
    public:
        std::string name;
        int lastUpdated;

    };

    class MovieDetail {
    public:
        std::string name;
        float score;
        std::vector<Actor> actors;
        std::string description;
    };

    class MovieController {
    private:
        std::vector<Movie *> _movies;
        std::map<std::string, MovieDetail *> _details;

    public:
        MovieController() {
            //populate data
            for (int i = 0; i < 10; i++) {
                auto movie = new Movie();
                movie->name = "Top Gun " + std::to_string(i);
                movie->lastUpdated = i * 10000;
                _movies.push_back(movie);

                auto movieDetail = new MovieDetail();
                movieDetail->name = movie->name;
                movieDetail->score = rand() % 10;
                movieDetail->description = "As students at the United States Navy's elite fighter weapons school compete to be best in the class, one daring young pilot learns a few things from a civilian instructor that are not taught in the classroom.";

                auto tomCruise = Actor();
                tomCruise.name = "Tom Cruise";
                tomCruise.age = 50;

                auto valKilmer = Actor();
                valKilmer.name = "Val Kilmer";
                valKilmer.age = 46;
                valKilmer.imageUrl = "https://m.media-amazon.com/images/M/MV5BMTk3ODIzMDA5Ml5BMl5BanBnXkFtZTcwNDY0NTU4Ng@@._V1_UY317_CR4,0,214,317_AL_.jpg";

                movieDetail->actors.push_back(tomCruise);
                movieDetail->actors.push_back(valKilmer);

                if (i % 2 == 0) {
                    auto timRobbins = Actor();
                    timRobbins.name = "Tim Robbins";
                    timRobbins.age = 55;
                    timRobbins.imageUrl = "https://m.media-amazon.com/images/M/MV5BMTI1OTYxNzAxOF5BMl5BanBnXkFtZTYwNTE5ODI4._V1_UY317_CR16,0,214,317_AL_.jpg";

                    movieDetail->actors.push_back(timRobbins);
                } else {
                    auto jenniferConnelly = Actor();
                    jenniferConnelly.name = "Jennifer Connelly";
                    jenniferConnelly.age = 39;
                    jenniferConnelly.imageUrl = "https://m.media-amazon.com/images/M/MV5BOTczNTgzODYyMF5BMl5BanBnXkFtZTcwNjk4ODk4Mw@@._V1_UY317_CR12,0,214,317_AL_.jpg";

                    movieDetail->actors.push_back(jenniferConnelly);
                }

                _details[movie->name] = movieDetail;
            }
        }

        //Returns list of movies
        std::vector<Movie *> getMovies() {
            return _movies;
        }

        //Returns details about a specific movie
        MovieDetail *getMovieDetail(std::string name) {
            for (auto item:_details) {
                if (item.second->name == name) {
                    return item.second;
                }
            }
            return nullptr;
        }


    };
}

#endif /* MovieController_hpp */

static movies::MovieController controller = movies::MovieController();

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_highrise_challenge_data_JniMoviesSource_getMoviesFromJNI(
        JNIEnv *env,
        jobject thiz
) {
    auto movies = controller.getMovies();

    jclass jniMovieClass = (env)->FindClass("com/highrise/challenge/data/models/JniMovie");
    jmethodID jniMovieConstructorId = env->GetMethodID(
            jniMovieClass,
            "<init>",
            "(Ljava/lang/String;I)V"
    );
    jobjectArray objArray = env->NewObjectArray(movies.size(), jniMovieClass, nullptr);
    for (int i = 0; i < movies.size(); i++) {
        movies::Movie movie = *movies[i];
        jobject jniMovie = env->NewObject(
                jniMovieClass,
                jniMovieConstructorId,
                env->NewStringUTF(movie.name.c_str()),
                movie.lastUpdated);
        env->SetObjectArrayElement(objArray, i, jniMovie);
    }

    return objArray;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_highrise_challenge_data_JniMovieDetailSource_getMovieDetailFromJNI(
        JNIEnv *env,
        jobject thiz,
        jstring name
) {
    const char *resultCStr = env->GetStringUTFChars(name, nullptr);
    std::string movieName(resultCStr);
    auto movieDetail = controller.getMovieDetail(movieName);
    if (movieDetail == nullptr) return nullptr;

    jclass jniActorClass = env->FindClass("com/highrise/challenge/data/models/JniActor");
    jmethodID jniActorConstructor = env->GetMethodID(
            jniActorClass,
            "<init>",
            "(Ljava/lang/String;ILjava/lang/String;)V"
    );
    jclass jniMovieDetailClass = env->FindClass(
            "com/highrise/challenge/data/models/JniMovieDetail");
    jmethodID jniMovieDetailConstructor = env->GetMethodID(
            jniMovieDetailClass,
            "<init>",
            "(Ljava/lang/String;F[Lcom/highrise/challenge/data/models/JniActor;Ljava/lang/String;)V"
    );

    auto actors = movieDetail->actors;
    jobjectArray jniActors = env->NewObjectArray(actors.size(), jniActorClass, nullptr);
    for (int i = 0; i < actors.size(); i++) {
        movies::Actor actor = actors[i];
        jobject jniActor = env->NewObject(
                jniActorClass,
                jniActorConstructor,
                env->NewStringUTF(actor.name.c_str()),
                actor.age,
                env->NewStringUTF(actor.imageUrl.c_str())
        );
        env->SetObjectArrayElement(jniActors, i, jniActor);
    }

    return env->NewObject(
            jniMovieDetailClass,
            jniMovieDetailConstructor,
            env->NewStringUTF(movieDetail->name.c_str()),
            movieDetail->score,
            jniActors,
            env->NewStringUTF(movieDetail->description.c_str())
    );
}