# OfflinePlaceListAppMVVMDagger
Sample app about listing the places and saving for offline display using MVVM arch with Dagger and Room DB


Used custom image loader for saving image bitmaps in DiskLRUCache for offline image display

Used own custom firebase functions api for sample place list data.

# Code Map :

MainActivity - > MainActivityViewModel <- MainActivityModule (Provider) <- ActivityBuilder (Provider)

CloudRepo (Cloud api calls) <- NetworkModule (Provider)

DBRepo (Local database calls) <- DBModule (Provider)

Place (Model) -> common model for Cloud and DB repos

CachedImageViewLoader - > Offline Image storage using DiskLruCache and LRUCache

# To achieve :

UnitTest cases for dagger2 code is different from non dagger MVVM codebase. 
The later can be written unittest cases feasibly.

Still must do proper R&D on writing testcases using dagger injection that executes livedata changes




